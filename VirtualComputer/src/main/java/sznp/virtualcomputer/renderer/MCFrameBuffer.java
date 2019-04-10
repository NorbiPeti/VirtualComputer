package sznp.virtualcomputer.renderer;

import com.sun.jna.Pointer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.mozilla.interfaces.IFramebuffer;
import org.mozilla.interfaces.IFramebufferOverlay;
import org.mozilla.interfaces.nsISupports;
import org.mozilla.xpcom.Mozilla;
import org.virtualbox_6_0.*;
import sznp.virtualcomputer.PluginMain;
import sznp.virtualcomputer.util.Timing;

import java.util.Arrays;

public class MCFrameBuffer implements IFramebuffer {
	private IDisplay display;
	private Holder<IDisplaySourceBitmap> holder = new Holder<>();

	public MCFrameBuffer(IDisplay display, boolean VBoxDirect) { //TODO: Implement param
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
		try {
			System.out.println("Capabilities queried");
			System.out.println("Capabilities: " + Arrays.toString(arg0));
			return new long[]{FramebufferCapabilities.UpdateImage.value()};
		}
		catch(Exception e) {
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

	private BukkitTask tt;
	private Pointer pointer;
	private int width;
	private int height;

	@Override
	public void notifyChange(long screenId, long xOrigin, long yOrigin, long width, long height) {
		if (tt != null)
			tt.cancel();
		tt = Bukkit.getScheduler().runTaskAsynchronously(PluginMain.Instance, () -> {
			try {
				display.querySourceBitmap(0L, holder);
				long[] ptr = new long[1], w = new long[1], h = new long[1], bpp = new long[1], bpl = new long[1], pf = new long[1];
				holder.value.getTypedWrapped().queryBitmapInfo(ptr, w, h, bpp, bpl, pf);
				if (PluginMain.direct) {
					pointer = new Pointer(ptr[0]);
					this.width = (int) w[0];
					this.height = (int) h[0];
					GPURenderer.update(pointer.getByteArray(0L, (int) (w[0] * h[0] * 4)), (int) w[0], (int) h[0], 0, 0, this.width, this.height);
				} else {
					PluginMain.allpixels = new Pointer(ptr[0]).getByteBuffer(0L, width * height * 4);
					if (width * height > 640 * 480)
						PluginMain.allpixels.limit(640 * 480 * 4);
					else
						PluginMain.allpixels.limit((int) (width * height * 4));
				}
			} catch (VBoxException e) {
				if (e.getResultCode() == 0x80070005)
					return; // Machine is being powered down
				if (e.getResultCode() == 0x80004005) //The function "querySourceBitmap" returned an error condition: "Operation failed (NS_ERROR_FAILURE)"
					System.out.println("I don't know why this happens, but stopping the computer helps.");
				e.printStackTrace();
			} catch (Throwable t) {
				t.printStackTrace();
			}
		});
	}

	@Override
	public void notifyUpdate(long x, long y, long width, long height) {
		Timing t = new Timing();
		GPURenderer.update(pointer.getByteArray(0L, this.width * this.height * 4), this.width, this.height, (int) x, (int) y, (int) width, (int) height);
		if (t.elapsedMS() > 60) //Typically 1ms max
			System.out.println("Update took " + t.elapsedMS() + "ms");
	}

	@Override
	public void notifyUpdateImage(long arg0, long arg1, long arg2, long arg3, byte[] arg4) {
		System.out.println("Update image!");
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
