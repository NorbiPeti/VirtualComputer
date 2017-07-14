package sznp.virtualcomputer;

import org.mozilla.interfaces.IFramebuffer;
import org.mozilla.interfaces.IFramebufferOverlay;
import org.mozilla.interfaces.nsISupports;
import org.virtualbox_5_1.BitmapFormat;
import org.virtualbox_5_1.FramebufferCapabilities;
import org.virtualbox_5_1.Holder;
import org.virtualbox_5_1.IDisplay;
import org.virtualbox_5_1.IDisplaySourceBitmap;

public class MCFrameBuffer implements IFramebuffer {
	private IDisplay display;
	private Holder<IDisplaySourceBitmap> holder;

	public MCFrameBuffer(IDisplay display) {
		this.display = display;
	}

	@Override
	public nsISupports queryInterface(String arg0) {
		return this;
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
		return new long[] { FramebufferCapabilities.UpdateImage.value() };
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
		return 0;
	}

	@Override
	public void notify3DEvent(long arg0, byte[] arg1) {
	}

	@Override
	public void notifyChange(long screenId, long xOrigin, long yOrigin, long width, long height) {
		display.querySourceBitmap(0L, holder); // TODO: Crashes here
		holder.value.getTypedWrapped().queryBitmapInfo(PluginMain.allpixels, new long[] { width },
				new long[] { height }, new long[] { getBitsPerPixel() }, new long[] { getBytesPerLine() },
				new long[] { getPixelFormat() }); // These are out params but whatever
		System.out.println("Change!");
	}

	@Override
	public void notifyUpdate(long arg0, long arg1, long arg2, long arg3) {
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
