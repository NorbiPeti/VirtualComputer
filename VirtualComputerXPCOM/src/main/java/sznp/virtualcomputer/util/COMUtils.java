package sznp.virtualcomputer.util;

import lombok.val;
import org.virtualbox_6_1.*;
import org.virtualbox_6_1.xpcom.IUnknown;
import sznp.virtualcomputer.COMFrameBuffer;
import sznp.virtualcomputer.EventHandler;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class COMUtils {
	private COMUtils() {
	}

	//public static void registerListener(IEventSource source, IEventListener listener, VBoxEventType... types) {
	public static org.virtualbox_6_1.IEventListener registerListener(IEventSource source, IEventHandler listener, List<VBoxEventType> types) {
		val ret = new org.virtualbox_6_1.IEventListener(new EventHandler(listener));
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
		return new IFramebuffer(new COMFrameBuffer(frameBuffer));
	}

	public static void queryBitmapInfo(IDisplaySourceBitmap bitmap, long[] ptr, long[] w, long[] h, long[] bpp, long[] bpl, long[] pf) {
		bitmap.getTypedWrapped().queryBitmapInfo(ptr, w, h, bpp, bpl, pf);
	}

	public static void querySourceBitmap(IDisplay display, Holder<IDisplaySourceBitmap> holder) {
		display.querySourceBitmap(0L, holder);
	}

	public static boolean convertToBool(boolean bool) {
		return bool;
	}

	public static boolean convertFromBool(boolean bool) {
		return bool;
	}

	public static long convertFromLong(long l) {
		return l;
	}
}
