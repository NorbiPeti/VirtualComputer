package sznp.virtualcomputer.util;

import lombok.val;
import org.mozilla.interfaces.IEventListener;
import org.virtualbox_6_0.IEvent;
import org.virtualbox_6_0.IEventSource;
import org.virtualbox_6_0.VBoxEventType;
import org.virtualbox_6_0.xpcom.IUnknown;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.List;

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

    //public static void registerListener(IEventSource source, IEventListener listener, VBoxEventType... types) {
    public static org.virtualbox_6_0.IEventListener registerListener(IEventSource source, IEventListener listener, List<VBoxEventType> types) {
        val ret = new org.virtualbox_6_0.IEventListener(listener);
        source.registerListener(ret, types, true);
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEvent> T getEvent(org.mozilla.interfaces.IEvent event, Class<T> cl) {
        //if (event.getType() != type.value()) return null;
        //return (T) T.queryInterface(new IEvent(event)); - Probably won't work
        try {
            val method = cl.getMethod("queryInterface", IUnknown.class);
            return (T) method.invoke(null, new IEvent(event));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
