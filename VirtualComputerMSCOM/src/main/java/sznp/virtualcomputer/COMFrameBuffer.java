package sznp.virtualcomputer;

import lombok.RequiredArgsConstructor;
import net.sf.jni4net.Ref;
import org.virtualbox_6_1.BitmapFormat;
import org.virtualbox_6_1.IFramebuffer;
import org.virtualbox_6_1.IFramebufferOverlay;
import system.Array;
import sznp.virtualcomputer.util.IMCFrameBuffer;
import virtualcomputerwindows.Exports;

import java.util.Arrays;

@RequiredArgsConstructor
public class COMFrameBuffer implements IFramebuffer {
	private final IMCFrameBuffer frameBuffer;

	public int getBitsPerPixel() {
		return 32;
	}

	public int getBytesPerLine() {
		return 640;
	}

	public Array getCapabilities_FixIt() {
		try {
			System.out.println("Capabilities queried");
			//return new long[]{FramebufferCapabilities.UpdateImage.value()};
			return Array.CreateInstance(system.Type.GetType("System.Int32"), 0);
		} catch (Exception e) {
			e.printStackTrace();
			return Array.CreateInstance(system.Type.GetType("System.Int32"), 0);
		}
	}

	public int getHeight() {
		return 480;
	}

	public int getHeightReduction() {
		return 0;
	}

	public IFramebufferOverlay getOverlay() {
		return null;
	}

	public BitmapFormat getPixelFormat_FixIt() {
		//return BitmapFormat.BGRA.value();
		return null;
	}

	public int getVisibleRegion(Ref<Byte> arg0, int arg1) {
		return 0;
	}

	public int getWidth() {
		return 640;
	}

	public long getWinId() {
		return 0; // Zero means no win id
	}

	public void notify3DEvent_FixIt(int type, Array data) {
		System.out.println("3D event! " + type + " - " + Arrays.toString(Exports.ConvertArrayByte(data)));
	}

	public void notifyChange(int screenId, int xOrigin, int yOrigin, int width, int height) {
		frameBuffer.notifyChange(screenId, xOrigin, yOrigin, width, height);
	}

	public void notifyUpdate(int x, int y, int width, int height) {
		frameBuffer.notifyUpdate(x, y, width, height);
	}

	public void notifyUpdateImage_FixIt(int arg0, int arg1, int arg2, int arg3, Array arg4) {
		frameBuffer.notifyUpdateImage(arg0, arg1, arg2, arg3, Exports.ConvertArrayByte(arg4));
	}

	public void setVisibleRegion(Ref<Byte> arg0, int arg1) {
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
	public void processVHWACommand(Ref<Byte> command, int enmCmd, int fromGuest) {

	}

	public int videoModeSupported(int arg0, int arg1, int arg2) {
		return 1;
	}
}
