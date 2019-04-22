package sznp.virtualcomputer;

import lombok.RequiredArgsConstructor;
import org.mozilla.interfaces.IFramebuffer;
import org.mozilla.interfaces.IFramebufferOverlay;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.Mozilla;
import org.virtualbox_6_0.BitmapFormat;
import org.virtualbox_6_0.FramebufferCapabilities;
import sznp.virtualcomputer.util.IMCFrameBuffer;

import java.util.Arrays;

@RequiredArgsConstructor
public class COMFrameBuffer implements IFramebuffer {
	private final IMCFrameBuffer frameBuffer;

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
		try {
			System.out.println("Capabilities queried");
			System.out.println("Capabilities: " + Arrays.toString(arg0));
			return new long[]{FramebufferCapabilities.UpdateImage.value()};
		} catch (Exception e) {
			e.printStackTrace();
			return new long[]{};
		}
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
		frameBuffer.notifyChange(screenId, xOrigin, yOrigin, width, height);
	}

	@Override
	public void notifyUpdate(long x, long y, long width, long height) {
		frameBuffer.notifyUpdate(x, y, width, height);
	}

	@Override
	public void notifyUpdateImage(long arg0, long arg1, long arg2, long arg3, byte[] arg4) {
		frameBuffer.notifyUpdateImage(arg0, arg1, arg2, arg3, arg4);
	}

	@Override
	public void setVisibleRegion(byte arg0, long arg1) {
	}

	@Override
	public void processVHWACommand(byte b, int i, boolean b1) {
	}

	@Override
	public boolean videoModeSupported(long arg0, long arg1, long arg2) {
		return true;
	}
}