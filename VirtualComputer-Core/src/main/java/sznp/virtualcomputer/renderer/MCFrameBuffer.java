package sznp.virtualcomputer.renderer;

import com.sun.jna.Pointer;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;
import org.virtualbox_6_1.Holder;
import org.virtualbox_6_1.IDisplay;
import org.virtualbox_6_1.IDisplaySourceBitmap;
import org.virtualbox_6_1.VBoxException;
import sznp.virtualcomputer.PluginMain;
import sznp.virtualcomputer.util.COMUtils;
import sznp.virtualcomputer.util.IMCFrameBuffer;
import sznp.virtualcomputer.util.Timing;

@RequiredArgsConstructor
public class MCFrameBuffer implements IMCFrameBuffer {
	private final IDisplay display;
	private final Holder<IDisplaySourceBitmap> holder = new Holder<>();
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
				COMUtils.queryBitmapInfo(holder.value, ptr, w, h, bpp, bpl, pf);
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
	public void notifyUpdateImage(long x, long y, long width, long height, byte[] image) {
		System.out.println("Update image!");
	}
}
