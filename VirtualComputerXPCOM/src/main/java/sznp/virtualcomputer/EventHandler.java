package sznp.virtualcomputer;

import org.mozilla.interfaces.IEvent;
import org.mozilla.interfaces.IEventListener;
import sznp.virtualcomputer.util.COMObjectBase;
import sznp.virtualcomputer.util.IEventHandler;

/**
 * A Bukkit-like event system which calls the appropriate methods on an event.
 */
public final class EventHandler extends COMObjectBase implements IEventListener {
    private final IEventHandler handler;
	private boolean enabled = true;

	/**
	 * New XPCOM event handler.
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
        handler.handleEvent(new org.virtualbox_6_0.IEvent(iEvent));
	}

	public void disable() {
		enabled = false;
	}
}
