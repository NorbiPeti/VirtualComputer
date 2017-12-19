package sznp.virtualcomputer;

import org.bukkit.Bukkit;
import org.mozilla.interfaces.IFramebuffer;
import org.mozilla.interfaces.IFramebufferOverlay;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.Mozilla;
import org.virtualbox_5_2.*;

public class MCFrameBuffer implements IFramebuffer {
	private IDisplay display;
	private Holder<IDisplaySourceBitmap> holder;

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

	@Override
	public void notifyChange(long screenId, long xOrigin, long yOrigin, long width, long height) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(PluginMain.Instance, () -> {
			display.querySourceBitmap(0L, holder); // TODO: Test if it crashes here
			byte[] arr = new byte[10];
			System.out.println("Arr0:" + arr[0]);
			holder.value.getTypedWrapped().queryBitmapInfo(arr, new long[] { width }, new long[] { height },
					new long[] { getBitsPerPixel() }, new long[] { getBytesPerLine() },
					new long[] { getPixelFormat() }); // These are out params but whatever
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
		for (IRenderer r : PluginMain.renderers)
			if (r instanceof DirectRenderer)
				((DirectRenderer) r).render(PluginMain.allpixels, x, y, width, height);
		System.out.println("Update!");
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
