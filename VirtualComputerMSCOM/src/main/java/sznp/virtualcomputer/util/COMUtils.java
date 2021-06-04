package sznp.virtualcomputer.util;

import com.jacob.com.COMConverter;
import com.jacob.com.Dispatch;
import com.jacob.com.Variant;
import lombok.SneakyThrows;
import lombok.val;
import lombok.var;
import org.virtualbox_6_1.*;
import virtualcomputerwindows.Exports;

import java.util.List;

public final class COMUtils {
	private COMUtils() {
	}

	public static IEventListener registerListener(IEventSource source, IEventHandler listener, List<VBoxEventType> types) {
		source.registerListener(, types, true);
	}

	@SneakyThrows
	public static <T extends IEvent> T getEvent(IEvent event, Class<T> cl) {
		return cl.getConstructor(Dispatch.class).newInstance(event.getTypedWrapped());
	}

	public static IFramebuffer gimmeAFramebuffer(IMCFrameBuffer frameBuffer) {
		var variant = new Variant();
		COMConverter.SetVariantAddress(variant, Exports.GetFrameBuffer(frameBuffer, COMConverter.GetVariantAddress(variant)));
		return new IFramebuffer(variant.toDispatch()); //Object variant
	}

	public static void queryBitmapInfo(IDisplaySourceBitmap bitmap, long[] ptr, long[] w, long[] h, long[] bpp, long[] bpl, long[] pf) {
		/*Out<Integer> wo = new Out<>(), ho = new Out<>(), bppo = new Out<>(), bplo = new Out<>();
		val pfo = new Out<org.virtualbox_6_1_FixIt.BitmapFormat>();
		val ptro = new Out<Long>();
		bitmap.queryBitmapInfo(ptro, wo, ho, bppo, bplo, pfo);
		ptr[0] = ptro.getValue();
		w[0] = wo.getValue();
		h[0] = ho.getValue();
		bpp[0] = bppo.getValue();
		bpl[0] = bplo.getValue();
		pf[0] = Exports.convertEnum(pfo.getValue());*/
	}

	public static void querySourceBitmap(IDisplay display, Holder<IDisplaySourceBitmap> holder) {
		display.querySourceBitmap(0L, holder);
	}
}
