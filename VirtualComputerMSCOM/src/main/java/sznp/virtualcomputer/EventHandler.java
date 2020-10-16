package sznp.virtualcomputer;

import org.virtualbox_6_1.IEvent;
import org.virtualbox_6_1.IEventListener;
import sznp.virtualcomputer.util.IEventHandler;

/**
 * A Bukkit-like event system which calls the appropriate methods on an event.
 */
public final class EventHandler implements IEventListener {
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

	@Override
	public final void handleEvent(IEvent iEvent) {
		if (!enabled)
			return;
        handler.handleEvent(iEvent);
	}

	public void disable() {
		enabled = false;
	}
}
