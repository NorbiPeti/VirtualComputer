package sznp.virtualcomputer.util;

import com.jacob.com.Variant;
import lombok.val;
import org.bukkit.Bukkit;
import org.mozilla.interfaces.IEventListener;
import org.virtualbox_6_0.IEvent;
import org.virtualbox_6_0.IEventSource;
import org.virtualbox_6_0.VBoxEventType;
import org.virtualbox_6_0.xpcom.IUnknown;
import sznp.virtualcomputer.events.EventHandlerBase;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Utils {
    /**
     * Adds the specified path to the java library path
     *
     * @param pathToAdd the path to add
     * @throws Exception
     */
    public static void addLibraryPath(String pathToAdd) throws Exception {
        final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
        usrPathsField.setAccessible(true);

        // get array of paths
        final String[] paths = (String[]) usrPathsField.get(null);

        // check if the path to add is already present
        for (String path : paths) {
            if (path.equals(pathToAdd)) {
                return;
            }
        }

        // add the new path
        final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
        newPaths[newPaths.length - 1] = pathToAdd;
        usrPathsField.set(null, newPaths);
    }

    public static org.virtualbox_6_0.IEventListener registerListener(IEventSource source, IEventListener listener, List<VBoxEventType> types) {
        val ret = new org.virtualbox_6_0.IEventListener(listener);
        source.registerListener(ret, types, true);
        return ret;
    }

    public static org.virtualbox_6_0.IEventListener registerListenerMSCOM(IEventSource source, Object listener, List<VBoxEventType> types) {
        val ret = new org.virtualbox_6_0.IEventListener(new Variant(listener).toDispatch());
        source.registerListener(ret, types, true);
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEvent> T getEvent(org.mozilla.interfaces.IEvent event, Class<T> cl) {
        try {
            val method = cl.getMethod("queryInterface", IUnknown.class);
            return (T) method.invoke(null, new IEvent(event));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEvent> T getEventMSCOM(Object event, Class<T> cl) {
        try {
            val method = cl.getMethod("queryInterface", org.virtualbox_6_0.mscom.IUnknown.class);
            return (T) method.invoke(null, new IEvent(new Variant(event).toDispatch()));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void handleEvent(EventHandlerBase handler, Map<VBoxEventType, Class<? extends IEvent>> eventMap, Object iEvent, boolean mscom) {
        Class<?> cl;
        if (mscom) {
            val kv = eventMap.entrySet().stream().filter(e -> e.getKey().value() == iEvent.getType().value()).findAny();
            if (!kv.isPresent()) return; //Event not supported
            cl = kv.get().getValue();
        } else {
            val kv = eventMap.entrySet().stream().filter(e -> e.getKey().value() == iEvent.getType()).findAny();
            if (!kv.isPresent()) return; //Event not supported
            cl = kv.get().getValue();
        }
        for (Method method : handler.getClass().getMethods()) {
            if (method.isAnnotationPresent(org.bukkit.event.EventHandler.class)
                    && method.getParameterCount() == 1 && method.getParameterTypes()[0] == cl) {
                try {
                    method.invoke(handler, Utils.getEvent(iEvent, cl));
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
}
