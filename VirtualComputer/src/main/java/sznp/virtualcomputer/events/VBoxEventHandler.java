package sznp.virtualcomputer.events;

import lombok.Getter;
import lombok.val;
import org.bukkit.command.CommandSender;
import org.mozilla.interfaces.IEvent;
import org.mozilla.interfaces.IEventListener;
import org.virtualbox_6_0.ISessionStateChangedEvent;
import org.virtualbox_6_0.SessionState;
import sznp.virtualcomputer.util.COMObjectBase;

public class VBoxEventHandler extends COMObjectBase implements IEventListener {
    public VBoxEventHandler() {
        instance = this;
    }

    @Getter
    private static VBoxEventHandler instance;
    private String machineID;
    private CommandSender sender;

    @Override
    public void handleEvent(IEvent iEvent) {
        if (iEvent instanceof ISessionStateChangedEvent) {
            val event = ((ISessionStateChangedEvent) iEvent);
            if (!event.getMachineId().equals(machineID)) return;
            if (event.getState() == SessionState.Locked) //Need to check here, because we can't access the console yet
                Computer.getInstance().onLock(sender);
        }
    }

    public void setup(String machineID, CommandSender sender) {
        this.machineID = machineID;
        this.sender = sender;
    }
}
