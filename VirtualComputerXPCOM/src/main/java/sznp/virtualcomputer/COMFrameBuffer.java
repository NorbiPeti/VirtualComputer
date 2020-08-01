package sznp.virtualcomputer;

import lombok.RequiredArgsConstructor;
import org.mozilla.interfaces.IFramebuffer;
import org.mozilla.interfaces.IFramebufferOverlay;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.Mozilla;
import org.virtualbox_6_1.BitmapFormat;
import org.virtualbox_6_1.FramebufferCapabilities;
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
	public void notify3DEvent(long type, byte[] data) {
		System.out.println("3D event! " + type + " - " + Arrays.toString(data));
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

	/**
	 * Posts a Video HW Acceleration Command to the frame buffer for processing.<br />
	 * <br />
	 * The commands used for 2D video acceleration (DDraw surface creation/destroying, blitting, scaling, color conversion, overlaying, etc.) are posted from quest to the host to be processed by the host hardware.
	 *
	 * @param command   Pointer to VBOXVHWACMD containing the command to execute.
	 * @param enmCmd    The validated VBOXVHWACMD::enmCmd value from the command.
	 * @param fromGuest Set when the command origins from the guest, clear if host.
	 */ //https://www.virtualbox.org/browser/vbox/trunk/src/VBox/Frontends/VirtualBox/src/VBoxFBOverlay.cpp#L4645
	@Override
	public void processVHWACommand(byte command, int enmCmd, boolean fromGuest) {

	}

	@Override
	public boolean videoModeSupported(long arg0, long arg1, long arg2) {
		return true;
	}
}
