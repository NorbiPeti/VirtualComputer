package com.mcplugindev.slipswhitley.sketchmap;

/*
 * This file was originally taken from https://github.com/slipswhitley/SketchMap
 */

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Base64;
import java.util.HashSet;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.map.MapView;

public class SketchMapUtils
{

	/**
	 * 
	 * Image Utils
	 * 
	 */

	public static BufferedImage resize(Image img, Integer width, Integer height)
	{

		img = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);

		if (img instanceof BufferedImage)
		{
			return (BufferedImage) img;
		}

		BufferedImage bimage = new BufferedImage(img.getWidth(null),
				img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

		Graphics2D bGr = bimage.createGraphics();
		bGr.drawImage(img, 0, 0, null);
		bGr.dispose();

		return bimage;
	}

	public static BufferedImage base64StringToImg(final String base64String)
	{
		try
		{
			return ImageIO.read(new ByteArrayInputStream(Base64.getDecoder()
					.decode(base64String)));
		} catch (final IOException ioe)
		{
			throw new UncheckedIOException(ioe);
		}
	}

	public static void sendColoredConsoleMessage(String msg)
	{
		ConsoleCommandSender sender = Bukkit.getConsoleSender();
		sender.sendMessage(msg);
	}

	/**
	 * Deprecated Methods Here :'c
	 */

	@SuppressWarnings("deprecation")
	public static short getMapID(MapView map)
	{
		return map.getId();
	}

	@SuppressWarnings("deprecation")
	public static MapView getMapView(short id)
	{
		MapView map = Bukkit.getMap(id);
		if (map != null)
		{
			return map;
		}

		return Bukkit.createMap(getDefaultWorld());
	}

	/**
	 * 
	 */

	public static Block getTargetBlock(Player player, int i)
	{
		return player.getTargetBlock((HashSet<Material>) null, i);
	}

	public static World getDefaultWorld()
	{
		return Bukkit.getWorlds().get(0);
	}

}
