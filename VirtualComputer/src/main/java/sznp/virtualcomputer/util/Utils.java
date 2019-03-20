package sznp.virtualcomputer.util;

import org.mozilla.interfaces.IEventListener;
import org.virtualbox_6_0.IEventSource;
import org.virtualbox_6_0.VBoxEventType;

import java.lang.reflect.Field;
import java.util.Arrays;

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

    public static void registerListener(IEventSource source, IEventListener listener, VBoxEventType... types) {
        source.registerListener(new org.virtualbox_6_0.IEventListener(listener), Arrays.asList(types), true);
    }
}
