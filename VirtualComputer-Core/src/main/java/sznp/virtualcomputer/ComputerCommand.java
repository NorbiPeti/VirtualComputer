package sznp.virtualcomputer;

import buttondevteam.lib.chat.Command2;
import buttondevteam.lib.chat.CommandClass;
import buttondevteam.lib.chat.ICommand2MC;
import org.bukkit.command.CommandSender;
import org.virtualbox_6_1.VBoxException;

@CommandClass
public class ComputerCommand extends ICommand2MC {
	@Command2.Subcommand
	public void def(CommandSender sender) {
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
}
