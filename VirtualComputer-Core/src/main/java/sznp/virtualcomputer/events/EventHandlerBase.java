package sznp.virtualcomputer.events;

import lombok.val;
import org.bukkit.Bukkit;
import org.virtualbox_6_0.IEvent;
import org.virtualbox_6_0.IEventSource;
import org.virtualbox_6_0.VBoxEventType;
import sznp.virtualcomputer.util.COMUtils;
import sznp.virtualcomputer.util.IEventHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

/**
 * A Bukkit-like event system which calls the appropriate methods on an event.
 */
public abstract class EventHandlerBase implements IEventHandler {
	/**
	 * The events to listen for. It will only look for these handlers.
	 */
	private final Map<VBoxEventType, Class<? extends org.virtualbox_6_0.IEvent>> eventMap;
	private boolean enabled = true;

	protected EventHandlerBase(Map<VBoxEventType, Class<? extends org.virtualbox_6_0.IEvent>> eventMap) {
		this.eventMap = eventMap;
	}

	@Override
	public final void handleEvent(IEvent iEvent) {
		if (!enabled)
			return;
		val kv = eventMap.entrySet().stream().filter(e -> e.getKey().value() == iEvent.getType().value()).findAny();
		if (!kv.isPresent()) return; //Event not supported
		val cl = kv.get().getValue();
		for (Method method : getClass().getMethods()) {
			if (method.isAnnotationPresent(org.bukkit.event.EventHandler.class)
					&& method.getParameterCount() == 1 && method.getParameterTypes()[0] == cl) {
				try {
					method.invoke(this, COMUtils.getEvent(iEvent, cl));
					return;
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					Bukkit.getLogger().warning("Error while handling VirtualBox event!");
					e.getCause().printStackTrace();
				}
			}
		}
	}

	public <T extends EventHandlerBase> org.virtualbox_6_0.IEventListener registerTo(IEventSource source) {
		return COMUtils.registerListener(source, this, new ArrayList<>(eventMap.keySet()));
	}

	public void disable() {
		enabled = false;
	}
}
