package sznp.virtualcomputer.util;

import com.jacob.activeX.ActiveXComponent;
import com.jacob.com.*;
import lombok.val;
import org.virtualbox_6_1.*;
import org.virtualbox_6_1.mscom.Helper;
import org.virtualbox_6_1.mscom.IUnknown;
import sznp.virtualcomputer.COMFrameBuffer;
import sznp.virtualcomputer.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class COMUtils {
    private COMUtils() {
    }

    //public static void registerListener(IEventSource source, IEventListener listener, VBoxEventType... types) {
    public static org.virtualbox_6_1.IEventListener registerListener(IEventSource source, IEventHandler listener, List<VBoxEventType> types) {
        //new DispatchEvents(source.getTypedWrapped(), listener);
        val ret = new org.virtualbox_6_1.IEventListener(new EventHandler(listener));
        /*com.jacob.activeX.ActiveXComponent.createNewInstance("IEventListener");
        new ActiveXComponent("");
        source.registerListener(ret, types, true);*/
        //registerListener(source, new EventHandler(listener), types, true);
        System.out.println("Testing listener...");
        ret.handleEvent(null);
        System.out.println("Tested");
        return ret;
        //return null;
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
        return new IFramebuffer(new Variant(new COMFrameBuffer(frameBuffer)).getDispatch());
    }

    public static void queryBitmapInfo(IDisplaySourceBitmap bitmap, long[] ptr, long[] w, long[] h, long[] bpp, long[] bpl, long[] pf) {
        Dispatch.call(bitmap.getTypedWrapped(), "queryBitmapInfo", ptr, w, h, bpp, bpl, pf);
    }
}
