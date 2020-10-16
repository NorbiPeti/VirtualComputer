package sznp.virtualcomputer.util;

import lombok.val;
import net.sf.jni4net.Out;
import org.virtualbox_6_1.*;
import sznp.virtualcomputer.COMFrameBuffer;
import sznp.virtualcomputer.EventHandler;
import virtualcomputerwindows.Exports;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

public final class COMUtils {
	private COMUtils() {
	}

	//public static void registerListener(IEventSource source, IEventListener listener, VBoxEventType... types) {
	public static IEventListener registerListener(IEventSource source, IEventHandler listener, List<VBoxEventType> types) {
		//new DispatchEvents(source.getTypedWrapped(), listener);
		val ret = new EventHandler(listener);
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
		//val method = cl.getMethod("queryInterface", IUnknown.class);
		//return (T) method.invoke(null, event);
		return null; //TODO
	}

	public static IFramebuffer gimmeAFramebuffer(IMCFrameBuffer frameBuffer) {
		return new COMFrameBuffer(frameBuffer);
	}

	public static void queryBitmapInfo(IDisplaySourceBitmap bitmap, long[] ptr, long[] w, long[] h, long[] bpp, long[] bpl, long[] pf) {
		Out<Integer> wo = new Out<>(), ho = new Out<>(), bppo = new Out<>(), bplo = new Out<>();
		val pfo = new Out<org.virtualbox_6_1_FixIt.BitmapFormat>();
		val ptro = new Out<Long>();
		bitmap.queryBitmapInfo(ptro, wo, ho, bppo, bplo, pfo);
		ptr[0] = ptro.getValue();
		w[0] = wo.getValue();
		h[0] = ho.getValue();
		bpp[0] = bppo.getValue();
		bpl[0] = bplo.getValue();
		pf[0] = Exports.convertEnum(pfo.getValue());
	}

	public static void querySourceBitmap(IDisplay display, Holder<IDisplaySourceBitmap> holder) {
		val out = new Out<IDisplaySourceBitmap>();
		display.querySourceBitmap(0, out);
		holder.value = out.getValue();
	}

	public static boolean convertToBool(int bool) {
		return bool != 0;
	}

	public static int convertFromBool(boolean bool) {
		return bool ? 1 : 0;
	}

	public static int convertFromLong(long l) {
		return (int) l;
	}
}
