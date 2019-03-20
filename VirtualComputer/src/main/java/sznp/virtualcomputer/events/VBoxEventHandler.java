package sznp.virtualcomputer.events;

import lombok.val;
import org.mozilla.interfaces.IEvent;
import org.mozilla.interfaces.IEventListener;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.Mozilla;
import org.virtualbox_6_0.IMachineStateChangedEvent;
import org.virtualbox_6_0.VBoxEventType;
import sznp.virtualcomputer.util.COMObjectBase;

public class VBoxEventHandler extends COMObjectBase implements IEventListener {
    @Override
    public void handleEvent(IEvent iEvent) {
        if(iEvent.getType()== VBoxEventType.OnMachineStateChanged.value()) {
            val event=(IMachineStateChangedEvent) iEvent;
        }
    }
}
