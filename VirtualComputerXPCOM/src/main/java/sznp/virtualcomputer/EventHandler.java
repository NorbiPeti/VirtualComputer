package sznp.virtualcomputer;

import org.mozilla.interfaces.IEvent;
import org.mozilla.interfaces.IEventListener;
import sznp.virtualcomputer.util.COMObjectBase;

import java.util.function.Consumer;

/**
 * A Bukkit-like event system which calls the appropriate methods on an event.
 */
public final class EventHandler extends COMObjectBase implements IEventListener {
	private final Consumer<org.virtualbox_6_0.IEvent> handler;
	private boolean enabled = true;

	/**
	 * New XPCOM event handler.
	 *
	 * @param handler The handle method that handles what needs to be handled
	 */
	public EventHandler(Consumer<org.virtualbox_6_0.IEvent> handler) {
		this.handler = handler;
	}

	@Override
	public final void handleEvent(IEvent iEvent) {
		if (!enabled)
			return;
		handler.accept(new org.virtualbox_6_0.IEvent(iEvent));
	}

	public void disable() {
		enabled = false;
	}
}
