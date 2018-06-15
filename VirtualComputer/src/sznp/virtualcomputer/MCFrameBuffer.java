package sznp.virtualcomputer;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.mozilla.interfaces.IFramebuffer;
import org.mozilla.interfaces.IFramebufferOverlay;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.Mozilla;
import org.virtualbox_5_2.BitmapFormat;
import org.virtualbox_5_2.Holder;
import org.virtualbox_5_2.IDisplay;
import org.virtualbox_5_2.IDisplaySourceBitmap;

public class MCFrameBuffer implements IFramebuffer {
	private IDisplay display;
	private Holder<IDisplaySourceBitmap> holder = new Holder<>();

	public MCFrameBuffer(IDisplay display) {
		this.display = display;
	}

	@Override
	public nsISupports queryInterface(String id) {
		return Mozilla.queryInterface(this, id);
	}

	@Override
	public long getBitsPerPixel() {
		return 32;
	}

	@Override
	public long getBytesPerLine() {
		return 640L;
	}

	@Override
	public long[] getCapabilities(long[] arg0) {
		return new long[] {};
	}

	@Override
	public long getHeight() {
		return 480;
	}

	@Override
	public long getHeightReduction() {
		return 0;
	}

	@Override
	public IFramebufferOverlay getOverlay() {
		return null;
	}

	@Override
	public long getPixelFormat() {
		return BitmapFormat.BGRA.value();
	}

	@Override
	public long getVisibleRegion(byte arg0, long arg1) {
		return 0;
	}

	@Override
	public long getWidth() {
		return 640;
	}

	@Override
	public long getWinId() {
		return 0; // Zero means no win id
	}

	@Override
	public void notify3DEvent(long arg0, byte[] arg1) {
	}

	private BukkitTask tt;
	private BukkitTask tttt;

	@Override
	public void notifyChange(long screenId, long xOrigin, long yOrigin, long width, long height) {
		System.out.println("Change - " + width + "x" + height);
		if (tt != null)
			tt.cancel();
		/*
		 * if (width > 640 || height > 480) { tt = Bukkit.getScheduler().runTaskTimerAsynchronously(PluginMain.Instance, () -> display.setVideoModeHint(0L, true, false, 0, 0, 640L, 480L, 32L), 5, 5);
		 * return; // Don't even try to render too large resolutions }
		 */
		tt = Bukkit.getScheduler().runTaskLaterAsynchronously(PluginMain.Instance, () -> {
			display.querySourceBitmap(0L, holder);
			byte[] arr = PluginMain.allpixels.array();
			long[] w = new long[1], h = new long[1], bpp = new long[1], bpl = new long[1], pf = new long[1];
			holder.value.getTypedWrapped().queryBitmapInfo(arr, w, h, bpp, bpl, pf);
			System.out.println("Arr0:" + arr[0]);
			System.out.println("whbppbplpf: " + w[0] + " " + h[0] + " " + bpp[0] + " " + bpl[0] + " " + pf[0]);
			if (width * height > 640 * 480)
				PluginMain.allpixels.limit(640 * 480 * 4);
			else
				PluginMain.allpixels.limit((int) (width * height * 4));
			for (IRenderer r : PluginMain.renderers)
				if (r instanceof BukkitRenderer)
					((BukkitRenderer) r).setAllPixels(PluginMain.allpixels);
				else if (r instanceof DirectRenderer)
					((DirectRenderer) r).render(PluginMain.allpixels, xOrigin, yOrigin, width, height);
			System.out.println("Change!");
		}, 5); // Wait 1/4th of a second
	}

	@Override
	public void notifyUpdate(long x, long y, long width, long height) {
		if(tttt != null)
			tttt.cancel(); //We are getting updates, but the pixel array isn't updated - VB reacts slowly
		tttt = Bukkit.getScheduler().runTaskLaterAsynchronously(PluginMain.Instance, () -> {
			for (IRenderer r : PluginMain.renderers)
				if (r instanceof DirectRenderer)
					((DirectRenderer) r).render(PluginMain.allpixels, x, y, width, height);
		}, 5);
	}

	@Override
	public void notifyUpdateImage(long arg0, long arg1, long arg2, long arg3, byte[] arg4) {
		System.out.println("Update image!");
	}

	@Override
	public void processVHWACommand(byte arg0) {
	}

	@Override
	public void setVisibleRegion(byte arg0, long arg1) {
	}

	@Override
	public boolean videoModeSupported(long arg0, long arg1, long arg2) {
		return true;
	}
}
