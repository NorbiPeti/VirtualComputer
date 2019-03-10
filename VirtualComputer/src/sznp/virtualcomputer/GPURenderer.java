package sznp.virtualcomputer;

import com.aparapi.Kernel;
import com.aparapi.Range;
import lombok.Setter;
import net.minecraft.server.v1_12_R1.WorldMap;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.map.RenderData;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

import java.lang.reflect.Field;
import java.util.Map;

public class GPURenderer extends MapRenderer implements IRenderer {
	private byte[] buffer;
	private MapView map;
	private Kernel kernel;
	@Setter
	private static int width;
	private Range range;

	public GPURenderer(short id, World world, int mapx, int mapy) throws Exception {
		map = IRenderer.prepare(id, world);
		final Field field = map.getClass().getDeclaredField("renderCache");
		field.setAccessible(true);
		@SuppressWarnings("unchecked") final Map<CraftPlayer, RenderData> renderCache = (Map<CraftPlayer, RenderData>) field.get(map);

		RenderData render = renderCache.get(null);

		if (render == null)
			renderCache.put(null, render = new RenderData());

		this.buffer = render.buffer;

		kernel = new Kernel() {
			@Override
			public void run() {
				int mx = getGlobalId(0);
				int my = getGlobalId(1);
				int imgx = mx + mapx * 128;
				int imgy = my + mapy * 128;
				int imgi = imgy * width + imgx;
				buffer[my * 128 + mx] = matchColor(PluginMain.pixels[imgi]);
			}
		};
		range = Range.create2D(128, 128);

		map.addRenderer(this);
	}

	@Override
	public void render(MapView map, MapCanvas canvas, Player player) {
		try {
			if (width == 0) return; //TODO: Stop rendering after computer is stopped
			Field field = canvas.getClass().getDeclaredField("buffer");
			field.setAccessible(true);
			buffer = (byte[]) field.get(canvas);
			kernel.put(buffer).put(PluginMain.pixels).execute(range).get(buffer);
			field = map.getClass().getDeclaredField("worldMap");
			field.setAccessible(true);
			WorldMap wmap = (WorldMap) field.get(map);
			wmap.flagDirty(0, 0);
			wmap.flagDirty(127, 127); // Send the whole image - TODO: Only send changes
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private byte matchColor(int bgra) { //TODO
		return 48;
	}
}
