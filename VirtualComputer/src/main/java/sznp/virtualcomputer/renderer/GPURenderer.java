package sznp.virtualcomputer.renderer;

import net.minecraft.server.v1_12_R1.WorldMap;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import sznp.virtualcomputer.util.Timing;

import java.awt.*;
import java.lang.reflect.Field;

public class GPURenderer extends MapRenderer implements IRenderer {
	private byte[] buffer;
	private GPURendererInternal kernel;
	private WorldMap wmap;
	//Store at central location after conversion
	private static int[] colors_;

	public GPURenderer(short id, World world, int mapx, int mapy) throws Exception {
		MapView map = IRenderer.prepare(id, world);
		if (map == null) return; //Testing

		if (colors_ == null) {
			Field field = MapPalette.class.getDeclaredField("colors");
			field.setAccessible(true);
			Color[] cs = (Color[]) field.get(null);
			colors_ = new int[cs.length];
			for (int i = 0; i < colors_.length; i++) {
				colors_[i] = cs[i].getRGB();
			}
		}
		Field field = map.getClass().getDeclaredField("worldMap");
		field.setAccessible(true);
		wmap = (WorldMap) field.get(map);
		kernel = new GPURendererInternal(mapx, mapy, colors_);

		//System.setProperty("com.codegen.config.enable.NEW", "true");

		map.addRenderer(this);
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		Timing t = new Timing();
		try {
			if (kernel.isRendered()) return; //TODO: Stop rendering after computer is stopped
			if (buffer == null) { //The buffer remains the same, as the canvas remains the same
				Field field = canvas.getClass().getDeclaredField("buffer");
				field.setAccessible(true);
				buffer = (byte[]) field.get(canvas);
			}
			kernel.render(buffer);
			wmap.flagDirty(0, 0);
			wmap.flagDirty(127, 127); // Send the whole image - TODO: Only send changes
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (t.elapsedMS() > 60)
			System.out.println("Map rendering took " + t.elapsedMS() + "ms");
	}
}
