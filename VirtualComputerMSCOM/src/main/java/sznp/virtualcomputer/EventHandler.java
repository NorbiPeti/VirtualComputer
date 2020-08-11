package sznp.virtualcomputer;

import org.virtualbox_6_1.IEvent;
import sznp.virtualcomputer.util.COMObjectBase;
import sznp.virtualcomputer.util.IEventHandler;

/**
 * A Bukkit-like event system which calls the appropriate methods on an event.
 */
public final class EventHandler extends COMObjectBase {
    private final IEventHandler handler;
	private boolean enabled = true;

	/**
	 * New MSCOM event handler.
	 *
	 * @param handler The handle method that handles what needs to be handled
	 */
    public EventHandler(IEventHandler handler) {
		this.handler = handler;
	}

	public final void handleEvent(IEvent iEvent) {
		if (!enabled)
			return;
        handler.handleEvent(iEvent);
	}

	public void disable() {
		enabled = false;
	}
}
