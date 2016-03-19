package com.mcplugindev.slipswhitley.sketchmap.map;

import java.awt.image.BufferedImage;
import java.util.concurrent.TimeUnit;

import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;

public class ImageRenderer extends MapRenderer
{

	private BufferedImage image;

	//private Boolean imageRendered;

	public ImageRenderer(BufferedImage image)
	{
		this.image = image;
		//this.imageRendered = false;
	}

	private int progress = 0;
	//private final int updatepixels = 20;
	public static int updatepixels = 15;

	@Override
	public void render(MapView view, MapCanvas canvas, Player player)
	{
		/*
		 * if(imageRendered) {
		 * return;
		 * }
		 */

		long time = System.nanoTime();

		try
		{
			//if (progress != 128 / updatepixels) //<-- Never had an issue with this before 2016.02.20. because I wasn't using 16 as updatepixels
			canvas.drawImage(0, progress * updatepixels, image.getSubimage(0,
					progress * updatepixels, 128, (progress * updatepixels
							+ updatepixels >= 128 ? 128 - progress
							* updatepixels : updatepixels)));
			if (progress < 128 / updatepixels)
				progress++;
			else
				progress = 0;
			//this.imageRendered = true;

			long diff = System.nanoTime() - time;
			if (TimeUnit.NANOSECONDS.toMillis(diff) > 40)
			{
				System.out.println("Map rendering took "
						+ TimeUnit.NANOSECONDS.toMillis(diff) + " ms");
				/*
				 * if (progress == 0 && updatepixels > 5)
				 * updatepixels--;
				 */
			} /*
			 * else if (TimeUnit.NANOSECONDS.toMillis(diff) < 25)
			 * if (progress == 0 && updatepixels < 50)
			 * updatepixels++;
			 */
			/*
			 * if (progress >= 128 / updatepixels)
			 * progress = 0;
			 */
		} catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("Progess: " + progress);
			System.out.println("UpdatePixels: " + updatepixels);
		}
	}
}
