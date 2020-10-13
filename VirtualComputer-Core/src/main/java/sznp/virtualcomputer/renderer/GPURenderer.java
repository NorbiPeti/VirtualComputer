package sznp.virtualcomputer.renderer;

import com.aparapi.device.Device;
import com.aparapi.internal.kernel.KernelManager;
import lombok.val;
import lombok.var;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import sznp.virtualcomputer.PluginMain;
import sznp.virtualcomputer.util.Timing;

import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.function.BiConsumer;

public class GPURenderer extends MapRenderer implements IRenderer {
	private byte[] buffer;
	private final GPURendererInternal kernel;
	private int mapx, mapy;
	//Store at central location after conversion
	private static int[] colors_;
	private int changedX = 0, changedY = 0, changedWidth = 640, changedHeight = 480;
	private BiConsumer<Integer, Integer> flagDirty; //This way it's version independent, as long as it's named the same
	private static ArrayList<GPURenderer> renderers = new ArrayList<>();
	private static Method flagDirtyMethod;
	private static boolean enabled = true;

	public GPURenderer(short id, World world, int mapx, int mapy) throws Exception {
		MapView map = IRenderer.prepare(id, world);
		if (map == null) {
			kernel = null;
			return; //Testing
		}

		if (colors_ == null) {
			Field field = MapPalette.class.getDeclaredField("colors");
			field.setAccessible(true);
			Color[] cs = (Color[]) field.get(null);
			colors_ = new int[cs.length];
			for (int i = 0; i < colors_.length; i++) {
				colors_[i] = cs[i].getRGB();
			}
		}
		this.mapx = mapx;
		this.mapy = mapy;
		Field field = map.getClass().getDeclaredField("worldMap");
		field.setAccessible(true);
		val wmap = field.get(map);
		if (flagDirtyMethod == null)
			flagDirtyMethod = wmap.getClass().getMethod("flagDirty", int.class, int.class);
		flagDirty = (x, y) -> {
			try {
				flagDirtyMethod.invoke(wmap, x, y);
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
		kernel = new GPURendererInternal(mapx, mapy, colors_);
		var dev = kernel.getTargetDevice();
		if (mapx == mapy && mapx == 0)
			PluginMain.Instance.getLogger().info("Using device: " + dev.getShortDescription());
		renderers.add(this);

		map.addRenderer(this);
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		if (!enabled) return;
		Timing t = new Timing();
		try {
			if (kernel.isRendered()) return;
			if (buffer == null) { //The buffer remains the same, as the canvas remains the same
				Field field = canvas.getClass().getDeclaredField("buffer");
				field.setAccessible(true);
				buffer = (byte[]) field.get(canvas);
			}
			if (mapx == 0 && mapy == 0) { //Only print once
				if (kernel.getTargetDevice().getType() != Device.TYPE.GPU) {
					PluginMain.Instance.getLogger().warning("Cannot use GPU! Target device: " + kernel.getTargetDevice().getShortDescription()
							+ " - Best device: " + KernelManager.instance().bestDevice().getShortDescription());
					PluginMain.Instance.getLogger().warning("Server performance may be affected"); //TODO: Index 0 out of range 0
				}
			}
			if (!PluginMain.sendAll) {
				synchronized (kernel) {
					if (changedX >= (mapx + 1) * 128 || changedY >= (mapy + 1) * 128
							|| changedX + changedWidth < mapx * 128 || changedY + changedHeight < mapy * 128) {
						kernel.ignoreChange();
						return; //No change for this map
					}
				}
				//System.out.println("changed: (" + changedX + ", " + changedY + ") " + changedWidth + "x" + changedHeight);
				//System.out.println("map: (" + mapx + ", " + mapy + ")");
				int x = changedX - mapx * 128;
				int y = changedY - mapy * 128;
				if (x < 0) x = 0;
				if (y < 0) y = 0;
				int xx = x + changedWidth >= 128 ? 127 : x + changedWidth;
				int yy = y + changedHeight >= 128 ? 127 : y + changedHeight;
				//System.out.println("local: ("+x+", "+y+") "+w+"x"+h);
				kernel.render(buffer);
				flagDirty.accept(x, y);
				flagDirty.accept(xx, yy); // Send the changes only
				changedX = Integer.MAX_VALUE; //Finished rendering
				changedY = Integer.MAX_VALUE; //TODO: Render as soon as we receive new image
				changedWidth = -1; //Finished rendering
				changedHeight = -1;
			} else {
				kernel.render(buffer);
				flagDirty.accept(0, 0);
				flagDirty.accept(127, 127); // Send everything
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (t.elapsedMS() > 60)
			PluginMain.Instance.getLogger().warning("Map rendering took " + t.elapsedMS() + "ms");
		if (t.elapsedMS() > 2000) {
			PluginMain.Instance.getLogger().severe("Map rendering is taking too long! Disabling rendering to prevent the server from crashing.");
			PluginMain.Instance.getLogger().severe("Make sure the server has root privileges or disable GPU rendering.");
			enabled = false;
		}
	}

	public static void update(byte[] pixels, int width, int height, int changedX, int changedY, int changedWidth, int changedHeight) {
		for (GPURenderer r : renderers) {
			synchronized (r.kernel) {
				if (!PluginMain.sendAll) {
					if (changedX < r.changedX)
						r.changedX = changedX;
					if (changedY < r.changedY)
						r.changedY = changedY;
					if (changedWidth > r.changedWidth)
						r.changedWidth = changedWidth;
					if (changedHeight > r.changedHeight)
						r.changedHeight = changedHeight;
				}
				r.kernel.setPixels(pixels, width, height);
			}
		}
	}
}
