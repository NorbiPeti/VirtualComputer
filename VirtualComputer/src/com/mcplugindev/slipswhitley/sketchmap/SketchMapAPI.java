package com.mcplugindev.slipswhitley.sketchmap;

/*
 * This file was originally taken from https://github.com/slipswhitley/SketchMap
 */

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.mcplugindev.slipswhitley.sketchmap.file.SketchMapFileException;
import com.mcplugindev.slipswhitley.sketchmap.map.RelativeLocation;
import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap;

//import com.mcplugindev.slipswhitley.sketchmap.map.SketchMap.BaseFormat;

public class SketchMapAPI
{

	public static SketchMap getMapByID(String id)
	{
		for (SketchMap map : SketchMap.getLoadedMaps())
		{
			if (map.getID().equalsIgnoreCase(id))
			{
				return map;
			}
		}

		return null;
	}

	public static List<ItemStack> getOrderedItemSet(SketchMap map)
	{
		List<ItemStack> items = new ArrayList<ItemStack>();

		for (int y = 0; y < map.getLengthY(); y++)
		{
			for (int x = 0; x < map.getLengthX(); x++)
			{
				for (RelativeLocation loc : map.getMapCollection().keySet())
				{
					if (loc.getX() != x || loc.getY() != y)
					{
						continue;
					}

					ItemStack iStack = new ItemStack(Material.MAP, 1);
					iStack.setDurability(SketchMapUtils.getMapID(map
							.getMapCollection().get(loc)));

					ItemMeta iMeta = iStack.getItemMeta();
					iMeta.setDisplayName(ChatColor.GREEN + "SketchMap ID: "
							+ ChatColor.GOLD + map.getID() + ChatColor.GREEN
							+ " Pos-X: " + ChatColor.GOLD + (x + 1)
							+ ChatColor.GREEN + " Pos-Y: " + ChatColor.GOLD
							+ (y + 1));

					iMeta.setLore(Arrays.asList(new String[] { ChatColor.GRAY
							+ "SketchMap ID: " + map.getID()

					}));

					iStack.setItemMeta(iMeta);
					items.add(iStack);

				}
			}
		}

		return items;
	}

	public static SketchMap loadSketchMapFromFile(File file)
			throws SketchMapFileException
	{

		YamlConfiguration config = null;
		try
		{
			config = YamlConfiguration.loadConfiguration(file);
		} catch (Exception ex)
		{
			throw new SketchMapFileException("Invalid SketchMap File \""
					+ file.getName() + "\"");
		}

		String[] fieldSet = { "x-panes", "y-panes", "public-protected",
				"map-collection", "base-format", "map-image", };

		for (String field : fieldSet)
		{
			if (!config.isSet(field))
			{
				throw new SketchMapFileException(
						"Unable to load SketchMap file \"" + file.getName()
								+ "\" missing field \"" + field + "\"");
			}
		}

		Integer xPanes = config.getInt("x-panes");
		if (xPanes == null || xPanes < 1)
		{
			throw new SketchMapFileException("Unable to load SketchMap file \""
					+ file.getName() + "\" invalid field \"x-panes\"");
		}

		Integer yPanes = config.getInt("y-panes");
		if (yPanes == null || yPanes < 1)
		{
			throw new SketchMapFileException("Unable to load SketchMap file \""
					+ file.getName() + "\" invalid field \"y-panes\"");
		}

		Boolean publicProtected = config.getBoolean("public-protected");
		if (publicProtected == null)
		{
			throw new SketchMapFileException("Unable to load SketchMap file \""
					+ file.getName() + "\" invalid field \"public-protected\"");
		}

		List<String> mapList = config.getStringList("map-collection");
		if (mapList == null)
		{
			throw new SketchMapFileException("Unable to load SketchMap file \""
					+ file.getName() + "\" invalid field \"map-collection\"");
		}

		Map<Short, RelativeLocation> mapCollection = new HashMap<Short, RelativeLocation>();

		for (String map : mapList)
		{
			String[] split = map.split(" ");
			if (split.length != 2)
			{
				throw new SketchMapFileException(
						"Unable to load SketchMap file \"" + file.getName()
								+ "\" cannot parse field in \"map-colection\"");
			}

			RelativeLocation loc = RelativeLocation.fromString(split[0]);

			if (loc == null)
			{
				throw new SketchMapFileException(
						"Unable to load SketchMap file \"" + file.getName()
								+ "\" cannot parse field in \"map-colection\"");
			}

			Short id = null;
			try
			{
				id = Short.parseShort(split[1]);
			} catch (Exception ex)
			{
				throw new SketchMapFileException(
						"Unable to load SketchMap file \"" + file.getName()
								+ "\" cannot parse field in \"map-colection\"");
			}

			mapCollection.put(id, loc);
		}

		//BaseFormat format = null;
		/*
		 * try {
		 * format = BaseFormat.valueOf(config.getString("base-format"));
		 * }
		 * catch (Exception ex) {
		 * throw new SketchMapFileException("Unable to load SketchMap file \"" +
		 * file.getName()
		 * + "\" cannot parse BaseFormat from field \"base-format\"");
		 * }
		 */

		String b64Img = config.getString("map-image");
		if (b64Img == null)
		{
			throw new SketchMapFileException("Unable to load SketchMap file \""
					+ file.getName() + "\" invalid field \"map-image\"");
		}

		BufferedImage image = null;

		try
		{
			image = SketchMapUtils.base64StringToImg(b64Img);
		} catch (Exception ex)
		{
			throw new SketchMapFileException("Unable to load SketchMap file \""
					+ file.getName()
					+ "\" parse image from field \"map-image\"");
		}

		String imageID = file.getName().substring(0,
				file.getName().lastIndexOf("."));
		if (getMapByID(imageID) != null)
		{
			throw new SketchMapFileException("Unable to load SketchMap file \""
					+ file.getName()
					+ "\" A SketchMap by that ID already exists.");
		}

		return new SketchMap(image, imageID, yPanes, yPanes, publicProtected);
	}

}
