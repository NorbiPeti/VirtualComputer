package sznp.virtualcomputer;

import buttondevteam.lib.chat.Command2;
import buttondevteam.lib.chat.CommandClass;
import buttondevteam.lib.chat.ICommand2MC;
import lombok.var;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.virtualbox_6_1.VBoxException;

import javax.annotation.Nullable;
import java.io.File;
import java.util.Objects;

@CommandClass(helpText = {
		"Computer plugin",
		"§bTo see the computer's status do /c status",
		"§bTo see the available machines do /c list",
		"§bTo start one of them do /c start <index>",
		"§bYou can only have one machine running at a time."
}, permGroup = "computer")
public class ComputerCommand extends ICommand2MC {
	@Command2.Subcommand
	public void status(CommandSender sender) {
		Computer.getInstance().Status(sender);
	}

	@Command2.Subcommand(aliases = {"poweron", "on"}, helpText = {
			"Start",
			"Starts the given virtual machine or the first one by default.",
			"Use /c list to see the index of the machines."
	})
	public void start(CommandSender sender, @Command2.OptionalArg int index) {
		Computer.getInstance().Start(sender, index);
	}

	@Command2.Subcommand
	public void list(CommandSender sender) {
		Computer.getInstance().List(sender);
	}

	@Command2.Subcommand(aliases = {"poweroff", "off", "kill"})
	public void stop(CommandSender sender) {
		Computer.getInstance().Stop(sender);
	}

	@Command2.Subcommand(aliases = {"powerbtn", "pwrbtn"})
	public void powerbutton(CommandSender sender, @Command2.OptionalArg int index) {
		Computer.getInstance().PowerButton(sender, index);
	}

	@Command2.Subcommand(aliases = "restart")
	public void reset(CommandSender sender) {
		Computer.getInstance().Reset(sender);
	}

	@Command2.Subcommand(aliases = "save state")
	public void save(CommandSender sender) {
		Computer.getInstance().SaveState(sender);
	}

	@Command2.Subcommand(aliases = "fix screen")
	public void fix(CommandSender sender) {
		Computer.getInstance().FixScreen(sender);
	}

	@Command2.Subcommand(aliases = {"press", "presskey", "keypress"}, helpText = {
			"Press key",
			"Presses the specified key. Valid values for the last param are 'down', 'up' and amount of ticks to hold."
	})
	public void key(CommandSender sender, String key, @Command2.OptionalArg String stateorduration) {
		if (stateorduration == null) stateorduration = "";
		Computer.getInstance().PressKey(sender, key, stateorduration);
	}

	@Command2.Subcommand(helpText = {
			"Mouse event",
			"Move the mouse by the specified offset or press the given button."
	})
	public void mouse(CommandSender sender, String keyOrX, @Command2.OptionalArg int y, @Command2.OptionalArg int z, @Command2.OptionalArg int w) {
		try {
			if (y != 0 || z != 0 || w != 0) {
				int x = Integer.parseInt(keyOrX);
				Computer.getInstance().UpdateMouse(sender, x, y, z, w, "");
			} else
				Computer.getInstance().UpdateMouse(sender, 0, 0, 0, 0, keyOrX);
		} catch (VBoxException e) {
			throw e;
		} catch (Exception ignored) {
		}
	}

	@Command2.Subcommand(helpText = {
			"Show keyboard",
			"Displays a keyboard in chat that you can click on.",
			"Each keypress is actually a command so they will be logged by the server."
	})
	public void show_keyboard(CommandSender sender) {
		sender.sendMessage("§6Avoid entering sensitive info here unless you own the server");
		var cc = new TextComponent();
		for (int i = 0; i < 10; i++)
			cc.addExtra(keyComp(i + "", null));
		var row = new String[]{"Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P"};
		for (String s : row) cc.addExtra(keyComp(s, null));
		cc.addExtra(keyComp("-", "minus"));
		cc.addExtra(keyComp("=", "equals"));
		cc.addExtra(keyComp("Backspace", "backspace"));
		cc.addExtra(keyComp("Tab", "tab"));
		sender.spigot().sendMessage(cc);

		cc = new TextComponent();
		row = new String[]{"A", "S", "D", "F", "G", "H", "J", "K", "L"};
		for (String s : row) cc.addExtra(keyComp(s, null)); //TODO: File format for layouts

		cc.addExtra(keyComp("{", "bracketLeft"));
		cc.addExtra(keyComp("}", "bracketRight"));

		sender.spigot().sendMessage(cc);
	}

	private TextComponent keyComp(String name, @Nullable String code) {
		if (code == null) code = name;
		var tc = new TextComponent("[" + name + "] ");
		tc.setColor(ChatColor.AQUA);
		tc.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/c key " + code));
		return tc;
	}

	@Command2.Subcommand(helpText = {
			"Lock mouse",
			"Locks the mouse to where you're looking. If you move your mouse, the computer's mouse will be moved."
	})
	public void lock_mouse(Player player) {
		MouseLockerPlayerListener.toggleLock(player);
	}

	@Command2.Subcommand(helpText = {
			"Set mouse speed",
			"Sets the mouse speed when locked. The default is 5."
	})
	public void lock_speed(CommandSender sender, float speed) {
		MouseLockerPlayerListener.LockedSpeed = speed;
		sender.sendMessage("§aMouse speed set to " + MouseLockerPlayerListener.LockedSpeed);
	}

	@Command2.Subcommand(helpText = {
			"Spawn screen",
			"Spawns a computer screen near you. All of them show the same thing."
	})
	public void spawn(Player player) {
		var loc = player.getLocation();
		var world = Objects.requireNonNull(loc.getWorld());
		short id = PluginMain.Instance.startID.get();
		for (int j = PluginMain.MCY - 1; j >= 0; j--) {
			for (int i = 0; i < PluginMain.MCX; i++) {
				var block = world.getBlockAt(loc.getBlockX() + i, loc.getBlockY() + j, loc.getBlockZ());
				block.setType(Material.BLACK_WOOL);
				var frameLoc = block.getLocation().add(0, 0, 1);
				var map = new ItemStack(Material.FILLED_MAP, 1);
				var meta = ((MapMeta) map.getItemMeta());
				if (meta == null) throw new NullPointerException("Map meta is null for " + frameLoc);
				meta.setMapId(id++);
				map.setItemMeta(meta);
				world.spawn(frameLoc, ItemFrame.class).setItem(map);
			}
		}
	}

	@Command2.Subcommand(helpText = {
			"Set layout",
			"This command sets the keyboard layout used for /c show keyboard.",
			"Valid options are files in the layouts folder in the plugin's directory."
	})
	public void set_layout(CommandSender sender, String layout) {
		var lf = PluginMain.Instance.layoutFolder;
		if (!lf.mkdirs()) {
			sender.sendMessage("§cFailed to create layouts folder!");
			return;
		}
		var l = new File(lf, layout + ".yml");
		if (!l.exists()) {
			sender.sendMessage("§cThe file " + l + " does not exist.");
			return;
		}
		var yc = YamlConfiguration.loadConfiguration(l);
		var list = yc.getList("keyboard");
		if (list == null) {
			sender.sendMessage("§cThe 'keyboard' key is missing.");
			return;
		}
		for (var item : list) {
			System.out.println("item: " + item);
		}
	}
}
