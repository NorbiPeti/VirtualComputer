package sznp.virtualcomputer;

import buttondevteam.lib.chat.Command2;
import buttondevteam.lib.chat.CommandClass;
import buttondevteam.lib.chat.ICommand2MC;
import lombok.var;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.javatuples.Tuple;
import org.virtualbox_6_1.VBoxException;

import javax.annotation.Nullable;

@CommandClass
public class ComputerCommand extends ICommand2MC {
	@Command2.Subcommand
	public void status(CommandSender sender) {
		//TODO
	}

	@Command2.Subcommand(aliases = {"poweron", "on"})
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

	@Command2.Subcommand(aliases = "savestate")
	public void save(CommandSender sender) {
		Computer.getInstance().SaveState(sender);
	}

	@Command2.Subcommand(aliases = "fixscreen")
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
}
