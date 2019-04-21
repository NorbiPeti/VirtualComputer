package sznp.virtualcomputer.events;

import org.mozilla.interfaces.IEvent;
import org.mozilla.interfaces.IEventListener;
import org.virtualbox_6_0.IEventSource;
import org.virtualbox_6_0.VBoxEventType;
import sznp.virtualcomputer.PluginMain;
import sznp.virtualcomputer.util.COMObjectBase;
import sznp.virtualcomputer.util.Utils;

import java.util.ArrayList;
import java.util.Map;

/**
 * A Bukkit-like event system which calls the appropriate methods on an event.
 */
public abstract class EventHandlerBase extends COMObjectBase implements IEventListener {
	/**
	 * The events to listen for. It will only look for these handlers.
	 */
	private final Map<VBoxEventType, Class<? extends org.virtualbox_6_0.IEvent>> eventMap;
	private boolean enabled = true;

	protected EventHandlerBase(Map<VBoxEventType, Class<? extends org.virtualbox_6_0.IEvent>> eventMap) {
		//this.eventMap = eventMap.entrySet().stream().collect(Collectors.toMap(e -> e.getKey().value(), Map.Entry::getValue));
		this.eventMap = eventMap;
	}

	@Override
	public final void handleEvent(IEvent iEvent) {
		if (!enabled)
			return;
		Utils.handleEvent(this, eventMap, iEvent, false);
	}

	public <T extends EventHandlerBase> org.virtualbox_6_0.IEventListener registerTo(IEventSource source) {
		if (PluginMain.MSCOM)
			return Utils.registerListenerMSCOM(source, this, new ArrayList<>(eventMap.keySet()));
		else
			return Utils.registerListener(source, this, new ArrayList<>(eventMap.keySet()));
	}

	public void disable() {
		enabled = false;
	}
}
