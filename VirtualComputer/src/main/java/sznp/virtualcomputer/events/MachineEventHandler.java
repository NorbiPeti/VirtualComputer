package sznp.virtualcomputer.events;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.mozilla.interfaces.IEvent;
import org.mozilla.interfaces.IEventListener;
import org.virtualbox_6_0.IStateChangedEvent;
import sznp.virtualcomputer.util.COMObjectBase;

@RequiredArgsConstructor
public class MachineEventHandler extends COMObjectBase implements IEventListener {
    private final Computer computer;
    @Override
    public void handleEvent(IEvent iEvent) {
        if(iEvent instanceof IStateChangedEvent) {
            val event=(IStateChangedEvent) iEvent; //https://www.virtualbox.org/sdkref/_virtual_box_8idl.html#a80b08f71210afe16038e904a656ed9eb
            switch (event.getState()) {
                case Stuck:
                    computer.Stop(null);
                    break;
                case PoweredOff:
                case Saved:
                    computer.stopRendering();
            }
        }
    }
}
