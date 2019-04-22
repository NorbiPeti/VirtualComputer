package sznp.virtualcomputer.util;

import org.virtualbox_6_0.IEvent;

public interface IEventHandler {
	void handleEvent(IEvent iEvent);
}
