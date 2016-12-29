package com.mcplugindev.slipswhitley.sketchmap.map;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageRenderer extends MapRenderer // Modified
{

	private BufferedImage image;

	public ImageRenderer(BufferedImage image) {
		this.image = image;
	}

	private int progress = 0;
	public static int updatepixels = 15;

	@Override
	public void render(MapView view, MapCanvas canvas, Player player) {
		long time = System.nanoTime();

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
