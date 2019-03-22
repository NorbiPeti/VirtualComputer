package sznp.virtualcomputer.events;

import com.google.common.collect.ImmutableMap;
import org.bukkit.event.EventHandler;
import org.virtualbox_6_0.IProgressTaskCompletedEvent;
import org.virtualbox_6_0.IStateChangedEvent;
import org.virtualbox_6_0.VBoxEventType;
import sznp.virtualcomputer.Computer;

public class MachineEventHandler extends EventHandlerBase {
    private final Computer computer;

	public MachineEventHandler(Computer computer) {
		super(ImmutableMap.of(VBoxEventType.OnStateChanged, IStateChangedEvent.class,
				VBoxEventType.OnProgressTaskCompleted, IProgressTaskCompletedEvent.class));
		this.computer = computer;
	}

	@EventHandler
	public void handleStateChange(IStateChangedEvent event) { //https://www.virtualbox.org/sdkref/_virtual_box_8idl.html#a80b08f71210afe16038e904a656ed9eb
		switch (event.getState()) {
			case Stuck:
				computer.Stop(null);
				break;
			case PoweredOff:
			case Saved:
				computer.onMachineStop();
        }
    }
}
