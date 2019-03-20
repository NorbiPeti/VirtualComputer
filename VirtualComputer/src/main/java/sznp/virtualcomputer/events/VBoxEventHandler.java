package sznp.virtualcomputer.events;

import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.virtualbox_6_0.ISessionStateChangedEvent;
import org.virtualbox_6_0.SessionState;
import org.virtualbox_6_0.VBoxEventType;
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
        System.out.println("Session change event: " + event);
        System.out.println("ID1: " + event.getMachineId() + " - ID2: " + machineID);
        if (!event.getMachineId().equals(machineID)) return;
        System.out.println("State: " + event.getState());
        if (event.getState() == SessionState.Locked) //Need to check here, because we can't access the console yet
            Computer.getInstance().onLock(sender);
    }

    public void setup(String machineID, CommandSender sender) {
        this.machineID = machineID;
        this.sender = sender;
    }
}
