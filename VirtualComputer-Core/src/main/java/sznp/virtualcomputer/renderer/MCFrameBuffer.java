package sznp.virtualcomputer.renderer;

import com.sun.jna.Pointer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
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

import java.util.concurrent.atomic.AtomicBoolean;

@RequiredArgsConstructor
public class MCFrameBuffer implements IMCFrameBuffer {
	private final IDisplay display;
	private final Holder<IDisplaySourceBitmap> holder = new Holder<>();
	private BukkitTask tt;
	private Pointer pointer;
	private int width;
	private int height;
	@Getter
	@Setter
	private String id;
	private final AtomicBoolean shouldUpdate = new AtomicBoolean();
	private boolean running;

	@Override
	public void notifyChange(long screenId, long xOrigin, long yOrigin, long width, long height) {
		if (tt != null)
			tt.cancel();
		tt = Bukkit.getScheduler().runTaskAsynchronously(PluginMain.Instance, () -> {
			synchronized (this) { //If a change occurs twice, then wait for it
				try {
					//System.out.println("Change: " + xOrigin + " " + yOrigin + " - " + width + " " + height);
					display.querySourceBitmap(0L, holder);
					long[] ptr = new long[1], w = new long[1], h = new long[1], bpp = new long[1], bpl = new long[1], pf = new long[1];
					COMUtils.queryBitmapInfo(holder.value, ptr, w, h, bpp, bpl, pf);
					if (PluginMain.direct) {
						pointer = new Pointer(ptr[0]);
						this.width = (int) w[0];
						this.height = (int) h[0];
						//System.out.println("Actual sizes: " + this.width + " " + this.height);
						/*if (this.width > 1024 || this.height > 768)
							return;*/
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
				} /*finally {
					System.out.println("Change finished");
				}*/
			}
		});
	}

	@Override
	public void notifyUpdate(long x, long y, long width, long height) {
		/*if (this.width > 1024 || this.height > 768)
			return;*/
		if(shouldUpdate.get())
			return; //Don't wait for lock, ignore update since we're updating everything anyway - TODO: Not always
		synchronized (this) {
			shouldUpdate.set(true);
			notifyAll();
		}
	}

	@Override
	public void notifyUpdateImage(long x, long y, long width, long height, byte[] image) {
		System.out.println("Update image!");
	}

	public void start() {
		running = true;
		Bukkit.getScheduler().runTaskAsynchronously(PluginMain.Instance, () -> {
			try {
				while (running) {
					synchronized (this) {
						while (!shouldUpdate.get())
							wait(1000);
						if (pointer == null) {
							System.out.println("Screen pointer is null");
							shouldUpdate.set(false);
							continue;
						}
						if (!running) return;
						//System.out.println("Update: " + x + " " + y + " - " + width + " " + height);
						Timing t = new Timing(); //TODO: Add support for only sending changed fragments
						GPURenderer.update(pointer.getByteArray(0L, this.width * this.height * 4), this.width, this.height, (int) 0, (int) 0, (int) width, (int) height);
						if (t.elapsedMS() > 60) //Typically 1ms max
							System.out.println("Update took " + t.elapsedMS() + "ms");
						shouldUpdate.set(false);
				/*else
					System.out.println("Update finished");*/
					}
				}
			} catch (InterruptedException ignored) {
			}
		});
	}

	public void stop() {
		synchronized (this) {
			running = false;
			notifyAll();
		}
	}
}
