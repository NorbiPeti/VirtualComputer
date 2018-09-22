package sznp.virtualcomputer;

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
import java.nio.ByteBuffer;
import java.util.Map;

public class DirectRenderer implements IRenderer {
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
	 * @param startindex
	 *            The index to start from in allpixels
	 * @throws Exception
	 *             Usually happens on incompatibility
	 */
	public DirectRenderer(short id, World world, int startindex) throws Exception, Exception, Exception, Exception {
		map = IRenderer.prepare(id, world);
		final Field field = map.getClass().getDeclaredField("renderCache");
		field.setAccessible(true);
		@SuppressWarnings("unchecked")
		final Map<CraftPlayer, RenderData> renderCache = (Map<CraftPlayer, RenderData>) field.get(map);

		RenderData render = renderCache.get(null);

		if (render == null)
			renderCache.put(null, render = new RenderData());

		this.startindex = startindex;
		this.buffer = render.buffer;
		map.addRenderer(new DummyRenderer());
	}

	private final class DummyRenderer extends MapRenderer {
		@Override
		public void render(MapView map, MapCanvas canvas, Player player) {
			if(allpixels != null)
				DirectRenderer.this.render(allpixels, x, y, width, height); //Render after zeroing whole map
		}
	}

	private Exception ex;
	private ByteBuffer allpixels;
	private long x, y, width, height;
	private long lastrender;

	public void setAllpixels(ByteBuffer bb) {
		allpixels = bb;
	}

	@SuppressWarnings("deprecation")
	public void render(ByteBuffer allpixels, long x, long y, long width, long height) { // TODO
		this.allpixels = allpixels; //TODO: |CRASH| Prevent trying to read memory after computer is stopped
		this.x=x;
		this.y=y;
		this.width=width;
		this.height=height;
		if(System.nanoTime()-lastrender<100*1000*1000)
			return;
		lastrender=System.nanoTime();
		try {
			boolean hascolor=false;
			for (int i = startindex, j = 0; i < allpixels.limit() - 4 && j < buffer.length; i += 4, j++) {
				if (PluginMain.Instance.checkMachineNotRunning(null))
					return;
				buffer[j] = MapPalette.matchColor(new Color(Byte.toUnsignedInt(allpixels.get(i)), Byte.toUnsignedInt(allpixels.get(i + 1)), Byte.toUnsignedInt(allpixels.get(i + 2))));
				if(allpixels.get(i+2)>10)
					hascolor=true;
			}
			if(hascolor)
				System.out.println("Some color!");
			else return;
			final Field field = map.getClass().getDeclaredField("worldMap");
			field.setAccessible(true);
			WorldMap wmap = (WorldMap) field.get(map);
			wmap.flagDirty(0, 0);
			wmap.flagDirty(127, 127); // Send the whole image - TODO: Only send changes
			/*
			 * final Field fieldf = map.getClass().getDeclaredField("renderCache"); fieldf.setAccessible(true);
			 * @SuppressWarnings("unchecked") final Map<CraftPlayer, RenderData> renderCache = (Map<CraftPlayer, RenderData>) fieldf.get(map); RenderData render = renderCache.get(null);
			 * System.out.println("==: " + (buffer == render.buffer)); System.out.println("equals:" + Arrays.equals(buffer, render.buffer));
			 */
		} catch (Exception e) {
			if (ex != null && (e.getMessage() == ex.getMessage() //Checking for null with the ==
					|| (e.getMessage() != null && e.getMessage().equals(ex.getMessage()))))
				return;
			(ex = e).printStackTrace();
		}
	}
}
