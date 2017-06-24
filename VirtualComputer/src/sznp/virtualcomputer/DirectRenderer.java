package sznp.virtualcomputer;

import java.awt.Color;
import java.lang.reflect.Field;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.map.RenderData;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapView;

import net.minecraft.server.v1_12_R1.WorldMap;

public class DirectRenderer implements IRenderer {
	private byte[] allpixels;
	private int startindex;
	private byte[] buffer;
	private MapView map;

	/**
	 * Attempt to use version-specific implementation
	 * 
	 * @param id
	 *            The ID of the current map
	 * @param world
	 *            The world to create new maps in
	 * @param allpixels
	 *            The raw pixel data from the machine in BGRA format
	 * @param startindex
	 *            The index to start from in allpixels
	 * @throws Exception
	 *             Usually happens on incompatibility
	 */
	public DirectRenderer(short id, World world, byte[] allpixels, int startindex)
			throws Exception, Exception, Exception, Exception {
		map = IRenderer.prepare(id, world);
		final Field field = map.getClass().getDeclaredField("renderCache");
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		final Map<CraftPlayer, RenderData> renderCache = (Map<CraftPlayer, RenderData>) field.get(map);

		RenderData render = renderCache.get(null);

		if (render == null) {
			render = new RenderData();
			renderCache.put(null, render);
		}

		this.allpixels = allpixels;
		this.startindex = startindex;
		this.buffer = render.buffer;

		Bukkit.getScheduler().runTask(PluginMain.Instance, this::render);
	}

	private Exception ex;

	@SuppressWarnings("deprecation")
	public void render() {
		try {
			for (int i = startindex, j = 0; i < startindex + 128 * 128 && i < allpixels.length
					&& j < buffer.length; i += 4, j++)
				buffer[j] = MapPalette.matchColor(new Color(allpixels[i], allpixels[i + 1], allpixels[i + 2]));
			final Field field = map.getClass().getField("worldmap");
			field.setAccessible(true);
			WorldMap wmap = (WorldMap) field.get(map);
			wmap.flagDirty(0, 0);
			wmap.flagDirty(128, 128); // Send the whole image - TODO: Only send changes
		} catch (Exception e) {
			if (ex != null && e.getMessage().equals(ex.getMessage()))
				return;
			(ex = e).printStackTrace();
		}
	}
}
