package sznp.virtualcomputer.util;

import lombok.val;
import org.virtualbox_6_0.IEvent;
import org.virtualbox_6_0.IEventSource;
import org.virtualbox_6_0.IFramebuffer;
import org.virtualbox_6_0.VBoxEventType;
import org.virtualbox_6_0.xpcom.IUnknown;
import sznp.virtualcomputer.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class COMUtils {
    private COMUtils() {
    }

    //public static void registerListener(IEventSource source, IEventListener listener, VBoxEventType... types) {
    public static org.virtualbox_6_0.IEventListener registerListener(IEventSource source, IEventHandler listener, List<VBoxEventType> types) {
        val ret = new org.virtualbox_6_0.IEventListener(new EventHandler(listener));
        source.registerListener(ret, types, true);
        return ret;
    }

    @SuppressWarnings("unchecked")
    public static <T extends IEvent> T getEvent(IEvent event, Class<T> cl) {
        try {
            val method = cl.getMethod("queryInterface", IUnknown.class);
            return (T) method.invoke(null, event);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static IFramebuffer gimmeAFramebuffer(IMCFrameBuffer frameBuffer) {
        return new IFramebuffer(frameBuffer); //TODO
    }
}
