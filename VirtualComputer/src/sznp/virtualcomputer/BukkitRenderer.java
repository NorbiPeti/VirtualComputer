package sznp.virtualcomputer;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;

import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class BukkitRenderer extends MapRenderer implements IRenderer {
	private ByteBuffer allpixels;
	private BufferedImage image;
	private int startindex;

	public void setAllPixels(ByteBuffer allpixels) {
		this.allpixels = allpixels;
	}

	/**
	 * Generic implementation, should work on most versions
	 * 
	 * @param id
	 *            The ID of the current map
	 * @param world
	 *            The world to create new maps in
	 * @param allpixels
	 *            The raw pixel data from the machine in BGRA format
	 * @param startindex
	 *            The index to start from in allpixels
	 */
	public BukkitRenderer(short id, World world, int startindex) {
		MapView map = IRenderer.prepare(id, world);
		map.addRenderer(this);
		this.startindex = startindex;
		image = new BufferedImage(640, 480, BufferedImage.TYPE_INT_RGB);
	}

	private int progress = 0;
	public static int updatepixels = 15;

	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		if (allpixels == null)
			return;
		long time = System.nanoTime();

		final int[] a = ((DataBufferInt) image.getRaster().getDataBuffer()).getData(); // Directly update the bytes of the image

		// (byte) bgra to rgb (int)
		for (int i = startindex, j = 0; i < startindex + 128 * 128; i = i + 4, j++) {
			int b, g, r;

			b = allpixels.get(i) & 0xFF;
			g = allpixels.get(i + 1) & 0xFF;
			r = allpixels.get(i + 2) & 0xFF;

			a[j] = (r << 16) | (g << 8) | b;
		}

		try {
			canvas.drawImage(0, progress * updatepixels, image.getSubimage(0, progress * updatepixels, 128,
					(progress * updatepixels + updatepixels >= 128 ? 128 - progress * updatepixels : updatepixels)));
			if (progress < 128 / updatepixels)
				progress++;
			else
				progress = 0;

			long diff = System.nanoTime() - time;
			if (TimeUnit.NANOSECONDS.toMillis(diff) > 40) {
				System.out.println("Map rendering took " + TimeUnit.NANOSECONDS.toMillis(diff) + " ms");
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Progess: " + progress);
			System.out.println("UpdatePixels: " + updatepixels);
		}
	}
}
