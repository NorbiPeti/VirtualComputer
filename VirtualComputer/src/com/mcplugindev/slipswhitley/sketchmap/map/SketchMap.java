package com.mcplugindev.slipswhitley.sketchmap.map;

/*
 * This file was originally taken from https://github.com/slipswhitley/SketchMap
 */

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.map.MapView;

import com.mcplugindev.slipswhitley.sketchmap.SketchMapUtils;

public class SketchMap
{

	public BufferedImage image;
	private String mapID;
	private Integer xPanes;
	private Integer yPanes;
	private Boolean publicProtected;
	//private BaseFormat format;

	private Map<RelativeLocation, MapView> mapCollection;

	/**
	 * 
	 * Create SketchMap using New Maps
	 * 
	 */

	public SketchMap(BufferedImage image, String mapID, int xPanes, int yPanes,
			boolean publicProtected)
	{

		this.image = SketchMapUtils.resize(image, xPanes * 128, yPanes * 128);
		this.mapID = mapID;
		this.xPanes = xPanes;
		this.yPanes = yPanes;
		this.publicProtected = publicProtected;
		//this.format = format;

		this.mapCollection = new HashMap<RelativeLocation, MapView>();

		getLoadedMaps().add(this);
		loadSketchMap();
	}

	private void loadSketchMap()
	{
		for (int x = 0; x < xPanes; x++)
		{
			for (int y = 0; y < yPanes; y++)
			{
				initMap(x, y,
						Bukkit.createMap(SketchMapUtils.getDefaultWorld()));
			}
		}
	}

	/**
	 * 
	 * Create SketchMap using Specified Maps
	 * 
	 */

	public SketchMap(BufferedImage image, String mapID, int xPanes, int yPanes,
			boolean publicProtected, Map<Short, RelativeLocation> mapCollection)
	{

		this.image = SketchMapUtils.resize(image, xPanes * 128, yPanes * 128);
		this.mapID = mapID;
		this.xPanes = xPanes;
		this.yPanes = yPanes;
		this.publicProtected = publicProtected;
		//this.format = format;

		this.mapCollection = new HashMap<RelativeLocation, MapView>();

		getLoadedMaps().add(this);
		loadSketchMap(mapCollection);
	}

	private void loadSketchMap(Map<Short, RelativeLocation> mapCollection)
	{
		for (Short mapID : mapCollection.keySet())
		{
			RelativeLocation loc = mapCollection.get(mapID);

			initMap(loc.getX(), loc.getY(), SketchMapUtils.getMapView(mapID));
		}
	}

	/**
	 * 
	 * 
	 * 
	 */

	private void initMap(int x, int y, MapView mapView)
	{
		BufferedImage subImage = image.getSubimage(x * 128, y * 128, 128, 128);
		mapView.getRenderers().clear();
		mapView.addRenderer(new ImageRenderer(subImage));

		mapCollection.put(new RelativeLocation(x, y), mapView);
	}

	/**
	 * 
	 * Get Object information
	 * 
	 */

	public String getID()
	{
		return mapID;
	}

	public BufferedImage getImage()
	{
		return image;
	}

	public int getLengthX()
	{
		return xPanes;
	}

	public int getLengthY()
	{
		return yPanes;
	}

	public boolean isPublicProtected()
	{
		return publicProtected;
	}

	public Map<RelativeLocation, MapView> getMapCollection()
	{
		return mapCollection;
	}

	/*
	 * public BaseFormat getBaseFormat() {
	 * return format;
	 * }
	 */

	/**
	 * 
	 * Map Functions
	 * 
	 * 
	 */

	public void delete()
	{
		getLoadedMaps().remove(this);

		try
		{
			this.finalize();
		} catch (Throwable e)
		{
		}
	}

	/**
	 * 
	 * Static Methods
	 * 
	 */

	private static Set<SketchMap> sketchMaps;

	public static Set<SketchMap> getLoadedMaps()
	{
		if (sketchMaps == null)
		{
			sketchMaps = new HashSet<SketchMap>();
		}

		return sketchMaps;
	}

	/*
	 * public enum BaseFormat {
	 * PNG,
	 * JPEG;
	 * 
	 * public String getExtension() {
	 * if(this == BaseFormat.PNG) {
	 * return "png";
	 * }
	 * if(this == BaseFormat.JPEG) {
	 * return "jpg";
	 * }
	 * return null;
	 * }
	 * 
	 * public static BaseFormat fromExtension(String ext) {
	 * if(ext.equalsIgnoreCase("png")) {
	 * return BaseFormat.PNG;
	 * 
	 * }
	 * if(ext.equalsIgnoreCase("jpg")) {
	 * return BaseFormat.JPEG;
	 * }
	 * return null;
	 * }
	 * }
	 */

}
