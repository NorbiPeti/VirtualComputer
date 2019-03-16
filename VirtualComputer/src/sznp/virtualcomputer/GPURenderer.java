package sznp.virtualcomputer;

import lombok.val;
import net.minecraft.server.v1_12_R1.WorldMap;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.map.RenderData;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.awt.*;
import java.lang.reflect.Field;
import java.util.Map;

public class GPURenderer extends MapRenderer implements IRenderer {
	private byte[] buffer;
	private GPURendererInternal kernel;
	//Store at central location after conversion
	private static int[] colors_;

	public GPURenderer(short id, World world, int mapx, int mapy) throws Exception {
		MapView map = IRenderer.prepare(id, world);
		if (map == null) return; //Testing
		Field field = map.getClass().getDeclaredField("renderCache");
		field.setAccessible(true);
		@SuppressWarnings("unchecked") val renderCache = (Map<CraftPlayer, RenderData>) field.get(map);

		if (colors_ == null) {
			field = MapPalette.class.getDeclaredField("colors");
			field.setAccessible(true);
			Color[] cs = (Color[]) field.get(null);
			colors_ = new int[cs.length];
			for (int i = 0; i < colors_.length; i++) {
				colors_[i] = cs[i].getRGB(); //TODO: BGR or RGB?
			}
		}
		kernel = new GPURendererInternal(mapx, mapy, colors_);

		RenderData render = renderCache.get(null);

		if (render == null)
			renderCache.put(null, render = new RenderData());

		this.buffer = render.buffer;

		//System.setProperty("com.codegen.config.enable.NEW", "true");

		map.addRenderer(this);
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		try {
			if (kernel.isRendered()) return; //TODO: Stop rendering after computer is stopped
			Field field = canvas.getClass().getDeclaredField("buffer");
			field.setAccessible(true);
			buffer = (byte[]) field.get(canvas);
			kernel.render(buffer);
			field = map.getClass().getDeclaredField("worldMap");
			field.setAccessible(true);
			WorldMap wmap = (WorldMap) field.get(map);
			wmap.flagDirty(0, 0);
			wmap.flagDirty(127, 127); // Send the whole image - TODO: Only send changes
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
