package sznp.virtualcomputer.renderer;

import net.minecraft.server.v1_12_R1.WorldMap;
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
import java.util.ArrayList;

public class GPURenderer extends MapRenderer implements IRenderer {
	private byte[] buffer;
	private final GPURendererInternal kernel;
	private WorldMap wmap;
	private int mapx, mapy;
	//Store at central location after conversion
	private static int[] colors_;
	private int changedX = 0, changedY = 0, changedWidth = 640, changedHeight = 480;
	private static ArrayList<GPURenderer> renderers = new ArrayList<>();

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
		wmap = (WorldMap) field.get(map);
		kernel = new GPURendererInternal(mapx, mapy, colors_);
		renderers.add(this);

		map.addRenderer(this);
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		Timing t = new Timing();
		try {
			if (kernel.isRendered()) return;
			if (buffer == null) { //The buffer remains the same, as the canvas remains the same
				Field field = canvas.getClass().getDeclaredField("buffer");
				field.setAccessible(true);
				buffer = (byte[]) field.get(canvas);
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
				wmap.flagDirty(x, y);
				wmap.flagDirty(xx, yy); // Send the changes only
				changedX = Integer.MAX_VALUE; //Finished rendering
				changedY = Integer.MAX_VALUE; //TODO: Render as soon as we receive new image
				changedWidth = -1; //Finished rendering
				changedHeight = -1;
			} else {
				kernel.render(buffer);
				wmap.flagDirty(0, 0);
				wmap.flagDirty(127, 127); // Send everything
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (t.elapsedMS() > 60)
			System.out.println("Map rendering took " + t.elapsedMS() + "ms");
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
