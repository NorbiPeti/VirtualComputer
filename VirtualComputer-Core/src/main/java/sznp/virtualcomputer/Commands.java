package sznp.virtualcomputer;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.tree.CommandNode;
import lombok.val;
import me.lucko.commodore.Commodore;
import me.lucko.commodore.CommodoreProvider;
import me.lucko.commodore.file.CommodoreFileFormat;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.virtualbox_6_1.MouseButtonState;
import org.virtualbox_6_1.VBoxException;
import sznp.virtualcomputer.util.Scancode;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class Commands implements CommandExecutor, TabCompleter {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 0)
			return false;
		switch (args[0].toLowerCase()) {
			case "start":
			case "poweron":
			case "on":
			case "startup":
				int c = getMachineIndex(args);
				if (c == -1) {
					sender.sendMessage("§cUsage: /" + label + " start [index]");
					return true;
				}
				Computer.getInstance().Start(sender, c);
				break;
			case "list":
				Computer.getInstance().List(sender);
				break;
			case "stop":
			case "poweroff":
			case "off":
			case "shutdown":
			case "kill":
				Computer.getInstance().Stop(sender);
				break;
			case "powerbutton":
			case "pwrbtn":
			case "powerbtn":
				c = getMachineIndex(args);
				if (c == -1) {
					sender.sendMessage("§cUsage: /" + label + " powerbutton [index]");
					return true;
				}
				Computer.getInstance().PowerButton(sender, c);
				break;
			case "reset":
			case "restart":
				Computer.getInstance().Reset(sender);
				break;
			case "fix":
			case "fixscreen":
				Computer.getInstance().FixScreen(sender);
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
					Computer.getInstance().PressKey(sender, args[1], "");
				else
					Computer.getInstance().PressKey(sender, args[1], args[2]);
				break;
			case "mouse":
				boolean showusage = true;
				if (args.length < 6) {
					try {
						// Command overloading, because I can :P
						if (args.length > 4) // 4<x<6
						{
							Computer.getInstance().UpdateMouse(sender, Integer.parseInt(args[1]), Integer.parseInt(args[2]),
									Integer.parseInt(args[3]), Integer.parseInt(args[4]), "", false);
							showusage = false;
						} else {
							if (args.length == 3) {
								Computer.getInstance().UpdateMouse(sender, 0, 0, 0, 0, args[1], args[2].equals("down"));
								showusage = false;
							} else if (args.length == 2) {
								Computer.getInstance().UpdateMouse(sender, 0, 0, 0, 0, args[1]);
								showusage = false;
							}
						}
					} catch (VBoxException e) {
						e.printStackTrace();
					} catch (Exception ignored) { //It will show the usage here
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
								+ " [\"\",{\"text\":\" [CapsLock]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key CapsLock\"}},{\"text\":\" [A]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key A\"}},{\"text\":\" [S]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key S\"}},{\"text\":\" [D]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key D\"}},{\"text\":\" [F]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key F\"}},{\"text\":\" [G]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key G\"}},{\"text\":\" [H]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key H\"}},{\"text\":\" [J]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key J\"}},{\"text\":\" [K]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key K\"}},{\"text\":\" [L]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key L\"}},{\"text\":\" [Enter]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Enter\"}}]");
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName()
								+ " [\"\",{\"text\":\"           [Z]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Z\"}},{\"text\":\" [X]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key X\"}},{\"text\":\" [C]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key C\"}},{\"text\":\" [V]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key V\"}},{\"text\":\" [B]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key B\"}},{\"text\":\" [N]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key N\"}},{\"text\":\" [M]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key M\"}}]");
						Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), "tellraw " + sender.getName()
								+ " [\"\",{\"text\":\" [Ctrl]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key ControlLeft\"}},{\"text\":\" [Alt]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key AltLeft\"}},{\"text\":\" [Space]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key Space\"}},{\"text\":\" [AltGr]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key AltRight\"}},{\"text\":\" [Ctrl]\",\"color\":\"green\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/computer key ControlRight\"}}]");
						break;
					case "mouse":
						MouseLockerPlayerListener.toggleLock((Player) sender);
						break;
					case "mspeed":
					case "mousespeed":
						if (args.length < 3) {
							sender.sendMessage("§cUsage: /computer input mspeed <number>");
							return true;
						}
						try {
							MouseLockerPlayerListener.LockedSpeed = Float.parseFloat(args[2]);
						} catch (NumberFormatException e) {
							sender.sendMessage("§cThe speed must be a number.");
							break;
						}
						sender.sendMessage("§aMouse speed set to " + MouseLockerPlayerListener.LockedSpeed);
				}
				break;
			}
		}
		return true;
	}

	private boolean tabSetup = true;

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (CommodoreProvider.isSupported() && tabSetup) {
			tabSetup = false;
			new Object() {
				private void setup(Command command) {
					val com = CommodoreProvider.getCommodore(PluginMain.Instance);
					try {
						val node = CommodoreFileFormat.parse(PluginMain.Instance.getResource("computer.commodore"));
						CommandNode<Object> arg = RequiredArgumentBuilder.argument("index", IntegerArgumentType.integer()).build();
						replaceChildren(node.getChild("start"), arg);
						replaceChildren(node.getChild("on"), arg);
						arg = RequiredArgumentBuilder.argument("key", StringArgumentType.word())
								.suggests((context, builder) -> {
									Arrays.stream(Scancode.values()).map(Scancode::name)
											.map(name -> name.replace("sc_", "")).forEach(builder::suggest);
									return builder.buildFuture();
								}).build();
						replaceChildren(node.getChild("key"), arg);
						replaceChildren(node.getChild("press"), arg);
						arg = RequiredArgumentBuilder.argument("button", StringArgumentType.word())
								.suggests((context, builder) -> {
									Arrays.stream(MouseButtonState.values()).map(MouseButtonState::name).forEach(builder::suggest);
									return builder.buildFuture();
								}).build();
						replaceChildren(node.getChild("mouse"), arg);
						com.register(command, node);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				private void replaceChildren(CommandNode<Object> target, CommandNode<Object> node) {
					target.getChildren().clear();
					target.addChild(node);
				}
			}.setup(command);
		}
		return Collections.emptyList();
	}

	/**
	 * Checks the 2nd parameter
	 *
	 * @return The index of the machine or -1 on usage error
	 */
	private int getMachineIndex(String[] args) {
		int c = 0;
		if (args.length >= 2) {
			try {
				c = Integer.parseInt(args[1]);
			} catch (Exception ignored) {
				return -1;
			}
		}
		return c;
	}
}
