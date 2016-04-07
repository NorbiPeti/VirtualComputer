package sznp.virtualcomputer;

import net.countercraft.movecraft.craft.Craft;
import net.countercraft.movecraft.craft.CraftManager;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor
{

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label,
			String[] args)
	{
		switch (cmd.getName().toLowerCase())
		{
		case "computer":
		{
			if (args.length == 0)
				return false;
			switch (args[0].toLowerCase())
			{
			case "start":
				PluginMain.getPlugin(PluginMain.class).Start(sender);
				break;
			case "stop":
				PluginMain.getPlugin(PluginMain.class).Stop(sender);
				break;
			case "debug":
				World w = Bukkit.getWorlds().get(0);
				Craft[] crafts = CraftManager.getInstance().getCraftsInWorld(w);
				sender.sendMessage("World: " + w);
				sender.sendMessage("Crafts: " + crafts);
				sender.sendMessage("Craft type: "
						+ crafts[0].getType().getCraftName());
				sender.sendMessage("DX: " + crafts[0].getLastDX());
				sender.sendMessage("DY: " + crafts[0].getLastDY());
				sender.sendMessage("DZ: " + crafts[0].getLastDZ());
				sender.sendMessage("MouseSpeed: " + PluginMain.MouseSpeed);
				sender.sendMessage("Block: "
						+ Bukkit.getWorlds()
								.get(0)
								.getBlockAt(crafts[0].getMinX(),
										crafts[0].getMinY() - 1,
										crafts[0].getMinZ()).getType()); //Block: AIR
				break;
			case "powerbutton":
				PluginMain.getPlugin(PluginMain.class).PowerButton(sender);
				break;
			case "reset":
				PluginMain.getPlugin(PluginMain.class).Reset(sender);
				break;
			case "fix":
				PluginMain.getPlugin(PluginMain.class).FixScreen(sender);
				break;
			case "key":
				if (args.length < 2)
				{
					sender.sendMessage("§cUsage: /computer key <key> [down/up|interval]");
					return true;
				}
				if (args.length < 3)
					PluginMain.getPlugin(PluginMain.class).PressKey(sender,
							args[1], "");
				else
					PluginMain.getPlugin(PluginMain.class).PressKey(sender,
							args[1], args[2]);
				break;
			case "mouse":
				boolean showusage = true;
				if (args.length < 6)
				{
					//Command overloading, because I can :P
					if (args.length > 4) // 4<x<6
					{
						PluginMain.getPlugin(PluginMain.class).UpdateMouse(
								sender, Integer.parseInt(args[1]),
								Integer.parseInt(args[2]),
								Integer.parseInt(args[3]),
								Integer.parseInt(args[4]), "", false);
						showusage = false;
					} else
					{
						if (args.length == 3)
						{
							PluginMain.getPlugin(PluginMain.class).UpdateMouse(
									sender, 0, 0, 0, 0,
									args[1], (args[2].equals("down")));
							showusage = false;
						} else if (args.length == 2)
						{
							PluginMain.getPlugin(PluginMain.class).UpdateMouse(
									sender, 0, 0, 0, 0,
									args[1]);
							showusage = false;
						}
					}
				}
				if (showusage)
				{
					sender.sendMessage("§cUsage: /computer mouse <relx> <rely> <relz> <relw>");
					sender.sendMessage("§cOr: /computer mouse <button> [up/down]");
				}
				break;
			case "mspeed":
				if (args.length < 2)
				{
					sender.sendMessage("§cUsage: /computer mspeed <speed>");
					return true;
				}
				PluginMain.MouseSpeed = Integer.parseInt(args[1]);
				sender.sendMessage("Mouse speed set to " + args[1]);
				break;
			case "input":
			{
				if (!(sender instanceof Player))
				{
					sender.sendMessage("§cError: Only players can use this command.");
					return true;
				}
				if (args.length < 2)
				{
					sender.sendMessage("§cUsage: /computer input <key|mouse>");
					return true;
				}
				if (args[1].equalsIgnoreCase("key"))
				{
					Bukkit.getServer()
							.dispatchCommand(
									Bukkit.getConsoleSender(),
									"tellraw "
											+ sender.getName()
											+ " [\"\",{\"text\":\" [0]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D0\"}},{\"text\":\" [1]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D1\"}},{\"text\":\" [2]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D2\"}},{\"text\":\" [3]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D3\"}},{\"text\":\" [4]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D4\"}},{\"text\":\" [5]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D5\"}},{\"text\":\" [6]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D6\"}},{\"text\":\" [7]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D7\"}},{\"text\":\" [8]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D8\"}},{\"text\":\" [9]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D9\"}}]");
					Bukkit.getServer()
							.dispatchCommand(
									Bukkit.getConsoleSender(),
									"tellraw "
											+ sender.getName()
											+ " [\"\",{\"text\":\" [Tab]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Tab\"}},{\"text\":\" [Q]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Q\"}},{\"text\":\" [W]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key W\"}},{\"text\":\" [E]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key E\"}},{\"text\":\" [R]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key R\"}},{\"text\":\" [T]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key T\"}},{\"text\":\" [Y]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Y\"}},{\"text\":\" [U]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key U\"}},{\"text\":\" [I]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key I\"}},{\"text\":\" [O]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key O\"}},{\"text\":\" [P]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key P\"}}]");
					Bukkit.getServer()
							.dispatchCommand(
									Bukkit.getConsoleSender(),
									"tellraw "
											+ sender.getName()
											+ " [\"\",{\"text\":\" [CapsLock]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key CapsLock\"}},{\"text\":\" [A]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key A\"}},{\"text\":\" [S]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key S\"}},{\"text\":\" [D]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D\"}},{\"text\":\" [F]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key F\"}},{\"text\":\" [G]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key G\"}},{\"text\":\" [H]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key H\"}},{\"text\":\" [J]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key J\"}},{\"text\":\" [K]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key K\"}},{\"text\":\" [L]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key L\"}},{\"text\":\" [Return]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Return\"}}]");
					Bukkit.getServer()
							.dispatchCommand(
									Bukkit.getConsoleSender(),
									"tellraw "
											+ sender.getName()
											+ " [\"\",{\"text\":\"           [Z]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Z\"}},{\"text\":\" [X]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key X\"}},{\"text\":\" [C]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key C\"}},{\"text\":\" [V]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key V\"}},{\"text\":\" [B]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key B\"}},{\"text\":\" [N]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key N\"}},{\"text\":\" [M]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key M\"}}]");
					Bukkit.getServer()
							.dispatchCommand(
									Bukkit.getConsoleSender(),
									"tellraw "
											+ sender.getName()
											+ " [\"\",{\"text\":\" [Ctrl]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key ControlLeft\"}},{\"text\":\" [Alt]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key AltLeft\"}},{\"text\":\" [Space]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Space\"}},{\"text\":\" [AltGr]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key AltRight\"}},{\"text\":\" [Ctrl]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key ControlRight\"}}]");
				} else if (args[1].equalsIgnoreCase("mouse"))
				{
					MouseLockerPlayerListener.MouseLocked = !MouseLockerPlayerListener.MouseLocked;
					if (MouseLockerPlayerListener.MouseLocked)
						sender.sendMessage("§aMouse locked.");
					else
						sender.sendMessage("§bMouse unlocked.");
				}
				break;
			}
			}
			return true;
		}
		}
		return false;
	}
}
