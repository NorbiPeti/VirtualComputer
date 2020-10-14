package sznp.virtualcomputer.events;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.virtualbox_6_1.ISessionStateChangedEvent;
import org.virtualbox_6_1.SessionState;
import org.virtualbox_6_1.VBoxEventType;
import sznp.virtualcomputer.Computer;

public class VBoxEventHandler extends EventHandlerBase {
	public VBoxEventHandler() {
		super(ImmutableMap.of(VBoxEventType.OnSessionStateChanged, ISessionStateChangedEvent.class));
		instance = this;
	}

	@Getter
	private static VBoxEventHandler instance;
	private String machineID;
	private CommandSender sender;

	@EventHandler
	public void onSessionStateChange(ISessionStateChangedEvent event) {
		if (!event.getMachineId().equals(machineID)) return;
		try {
			if (event.getState() == SessionState.Locked) //Need to check here, because we can't access the console yet
				Computer.getInstance().onLock(sender);
		} catch (Exception e) {
			sender.sendMessage("§cFailed to start computer! See the console for more details.");
			throw e;
		}
	}

	public void setup(String machineID, CommandSender sender) {
		this.machineID = machineID;
		this.sender = sender;
	}
}
