package com.mcplugindev.slipswhitley.sketchmap;

/*
 * This file was originally taken from https://github.com/slipswhitley/SketchMap
 */

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.mcplugindev.slipswhitley.sketchmap.listener.PlayerListener;

public class SketchMapPlugin extends JavaPlugin
{
	private static SketchMapPlugin plugin;

	public void onEnable()
	{
		plugin = this;

		setupListeners();

		sendEnabledMessage();
	}

	private void sendEnabledMessage()
	{
		SketchMapUtils.sendColoredConsoleMessage(ChatColor.GREEN
				+ "|                                                   |");

		SketchMapUtils.sendColoredConsoleMessage(ChatColor.GREEN + "|        "
				+ ChatColor.AQUA + "SketchMap "
				+ this.getDescription().getVersion() + " has been Enabled!"
				+ ChatColor.GREEN + "          |");

		SketchMapUtils.sendColoredConsoleMessage(ChatColor.GREEN + "|        "
				+ ChatColor.AQUA + "  Authors: SlipsWhitley & Fyrinlight"
				+ ChatColor.GREEN + "       |");

		SketchMapUtils.sendColoredConsoleMessage(ChatColor.GREEN
				+ "|                                                   |");
	}

	private void setupListeners()
	{
		Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
	}

	public static SketchMapPlugin getPlugin()
	{
		return plugin;
	}

}
