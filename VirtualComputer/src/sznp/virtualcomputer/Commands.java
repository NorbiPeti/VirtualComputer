package sznp.virtualcomputer;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		switch (cmd.getName().toLowerCase()) {
		case "computer": {
			if (args.length == 0)
				return false;
			switch (args[0].toLowerCase()) {
			case "start":
			case "poweron":
			case "on":
			case "startup":
				PluginMain.Instance.Start(sender);
				break;
			case "stop":
			case "poweroff":
			case "off":
			case "shutdown":
			case "kill":
				PluginMain.Instance.Stop(sender);
				break;
			case "powerbutton":
			case "pwrbtn":
			case "powerbtn":
				PluginMain.Instance.PowerButton(sender);
				break;
			case "reset":
			case "restart":
				PluginMain.Instance.Reset(sender);
				break;
			case "fix":
			case "fixscreen":
				PluginMain.Instance.FixScreen(sender);
				break;
			case "key":
			case "press":
			case "presskey":
			case "keypress":
				if (args.length < 2) {
					sender.sendMessage("§cUsage: /computer key <key> [down/up|duration(ticks)]");
					return true;
				}
				if (args.length < 3)
					PluginMain.Instance.PressKey(sender, args[1], "");
				else
					PluginMain.Instance.PressKey(sender, args[1], args[2]);
				break;
			case "mouse":
				boolean showusage = true;
				if (args.length < 6) {
					// Command overloading, because I can :P
					if (args.length > 4) // 4<x<6
					{
						PluginMain.Instance.UpdateMouse(sender, Integer.parseInt(args[1]), Integer.parseInt(args[2]),
								Integer.parseInt(args[3]), Integer.parseInt(args[4]), "", false);
						showusage = false;
					} else {
						if (args.length == 3) {
							PluginMain.Instance.UpdateMouse(sender, 0, 0, 0, 0, args[1], args[2].equals("down"));
							showusage = false;
						} else if (args.length == 2) {
							PluginMain.Instance.UpdateMouse(sender, 0, 0, 0, 0, args[1]);
							showusage = false;
						}
					}
				}
				if (showusage) {
					sender.sendMessage("§cUsage: /computer mouse <relx> <rely> <relz> <relw>");
					sender.sendMessage("§cOr: /computer mouse <button> [up/down]");
				}
				break;
			case "input":
			case "show":
			case "showinput":
			case "shinput": {
				if (!(sender instanceof Player)) {
					sender.sendMessage("§cError: Only players can use this command.");
					return true;
				}
				if (args.length < 2) {
					sender.sendMessage("§cUsage: /computer input <key|mouse>");
					return true;
				}
				switch (args[1].toLowerCase()) {
				case "key":
				case "keyboard":
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName()
							+ " [\"\",{\"text\":\" [0]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D0\"}},{\"text\":\" [1]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D1\"}},{\"text\":\" [2]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D2\"}},{\"text\":\" [3]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D3\"}},{\"text\":\" [4]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D4\"}},{\"text\":\" [5]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D5\"}},{\"text\":\" [6]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D6\"}},{\"text\":\" [7]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D7\"}},{\"text\":\" [8]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D8\"}},{\"text\":\" [9]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D9\"}}]");
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName()
							+ " [\"\",{\"text\":\" [Tab]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Tab\"}},{\"text\":\" [Q]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Q\"}},{\"text\":\" [W]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key W\"}},{\"text\":\" [E]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key E\"}},{\"text\":\" [R]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key R\"}},{\"text\":\" [T]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key T\"}},{\"text\":\" [Y]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Y\"}},{\"text\":\" [U]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key U\"}},{\"text\":\" [I]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key I\"}},{\"text\":\" [O]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key O\"}},{\"text\":\" [P]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key P\"}}]");
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName()
							+ " [\"\",{\"text\":\" [CapsLock]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key CapsLock\"}},{\"text\":\" [A]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key A\"}},{\"text\":\" [S]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key S\"}},{\"text\":\" [D]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D\"}},{\"text\":\" [F]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key F\"}},{\"text\":\" [G]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key G\"}},{\"text\":\" [H]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key H\"}},{\"text\":\" [J]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key J\"}},{\"text\":\" [K]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key K\"}},{\"text\":\" [L]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key L\"}},{\"text\":\" [Return]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Return\"}}]");
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName()
							+ " [\"\",{\"text\":\"           [Z]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Z\"}},{\"text\":\" [X]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key X\"}},{\"text\":\" [C]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key C\"}},{\"text\":\" [V]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key V\"}},{\"text\":\" [B]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key B\"}},{\"text\":\" [N]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key N\"}},{\"text\":\" [M]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key M\"}}]");
					Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName()
							+ " [\"\",{\"text\":\" [Ctrl]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key ControlLeft\"}},{\"text\":\" [Alt]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key AltLeft\"}},{\"text\":\" [Space]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Space\"}},{\"text\":\" [AltGr]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key AltRight\"}},{\"text\":\" [Ctrl]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key ControlRight\"}}]");
				case "mouse":
					if (!(sender instanceof Player)) {
						sender.sendMessage("§cOnly ingame players can use this command.");
						return true;
					}
					if (!MouseLockerPlayerListener.LockedPlayers.contains(sender)) {
						MouseLockerPlayerListener.LockedPlayers.add((Player) sender);
						sender.sendMessage("§aMouse locked.");
					} else {
						MouseLockerPlayerListener.LockedPlayers.remove(sender);
						sender.sendMessage("§bMouse unlocked.");
					}
				case "mspeed":
				case "mousespeed":
					if (args.length < 3) {
						sender.sendMessage("§cUsage: /computer input mspeed <integer>");
						return true;
					}
					MouseLockerPlayerListener.LockedSpeed = Float.parseFloat(args[2]);
					sender.sendMessage("§aMouse speed set to " + MouseLockerPlayerListener.LockedSpeed);
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
