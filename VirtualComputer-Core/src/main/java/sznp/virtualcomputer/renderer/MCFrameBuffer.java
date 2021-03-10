package sznp.virtualcomputer.renderer;

import com.sun.jna.Pointer;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
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

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.logging.Logger;

public class MCFrameBuffer implements IMCFrameBuffer {
	private final IDisplay display;
	private final Holder<IDisplaySourceBitmap> holder = new Holder<>();
	private final Logger logger;
	private final PluginMain plugin;
	/**
	 * Whether the VM is running inside the server
	 */
	private final boolean embedded;
	/**
	 * Whether the GPU is being used to render
	 */
	private final boolean direct;
	private BukkitTask tt;
	/**
	 * Used when running embedded
	 */
	private Pointer pointer;
	/**
	 * Used when not running embedded
	 */
	private byte[] screenImage;
	/**
	 * Used when running in indirect mode, not embedded
	 */
	private ByteBuffer screenBuffer;
	private int width;
	private int height;
	@Getter
	@Setter
	private String id;
	private final AtomicBoolean shouldUpdate = new AtomicBoolean();
	private final AtomicIntegerArray updateParameters = new AtomicIntegerArray(4);
	private boolean running;

	/**
	 * Creates a new framebuffer that receives images from the VM and sends the image data to Minecraft.
	 *
	 * @param display The VM display to use - TODO: Multiple monitors
	 * @param plugin  The plugin
	 * @param direct  Whether the GPU rendering is used
	 */
	public MCFrameBuffer(IDisplay display, PluginMain plugin, boolean embedded, boolean direct) {
		this.display = display;
		this.plugin = plugin;
		this.logger = plugin.getLogger();
		this.embedded = embedded; //Don't change even if the config got updated while running
		this.direct = direct;
	}

	@Override
	public void notifyChange(long screenId, long xOrigin, long yOrigin, long width, long height) {
		if (tt != null)
			tt.cancel();
		tt = Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			synchronized (this) { //If a change occurs twice, then wait for it
				try {
					if (!embedded) { //Running separately
						this.width = (int) width;
						this.height = (int) height;
						if (screenImage == null || screenImage.length != width * height * 4) {
							screenImage = new byte[(int) (width * height * 4)];
							screenBuffer = ByteBuffer.wrap(screenImage);
						}
						updateScreen((int) xOrigin, (int) yOrigin, (int) width, (int) height);
						return;
					}
					display.querySourceBitmap(0L, holder);
					long[] ptr = new long[1], w = new long[1], h = new long[1], bpp = new long[1], bpl = new long[1], pf = new long[1];
					COMUtils.queryBitmapInfo(holder.value, ptr, w, h, bpp, bpl, pf);
					pointer = new Pointer(ptr[0]);
					this.width = (int) w[0];
					this.height = (int) h[0];
					updateScreen(0, 0, (int) width, (int) height);
				} catch (VBoxException e) {
					if (e.getResultCode() == 0x80070005)
						return; // Machine is being powered down
					if (e.getResultCode() == 0x80004005) //The function "querySourceBitmap" returned an error condition: "Operation failed (NS_ERROR_FAILURE)"
						System.out.println("I don't know why this happens, but stopping the computer helps.");
					e.printStackTrace();
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		});
	}

	@Override
	public void notifyUpdate(long x, long y, long width, long height) {
		if (shouldUpdate.get())
			return; //Don't wait for lock, ignore update since we're updating everything anyway - TODO: Not always
		synchronized (this) {
			shouldUpdate.set(true);
			updateParameters.set(0, (int) x);
			updateParameters.set(1, (int) y);
			updateParameters.set(2, (int) width);
			updateParameters.set(3, (int) height);
			notifyAll();
		}
	}

	@Override
	public void notifyUpdateImage(long x, long y, long width, long height, byte[] image) {
		System.out.println("Update image!");
		if (this.width == 0 || this.height == 0) {
			logger.warning("Received screen image before resolution change!");
			return;
		}
		for (int i = 0; i < height; i++) //Copy lines of the screen in a fast way
			System.arraycopy(image, (int) (i * width * 4), screenImage, (int) (x + y * this.width * 4), (int) width * 4);
		updateScreen((int) x, (int) y, (int) width, (int) height);
	}

	public void startEmbedded() {
		running = true;
		Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
			try {
				while (running) {
					synchronized (this) {
						while (!shouldUpdate.get())
							wait(1000);
						if (pointer == null) {
							logger.warning("Embedded screen data pointer is null");
							shouldUpdate.set(false);
							continue;
						}
						if (!running) return;
						updateScreen(updateParameters.get(0), updateParameters.get(1), updateParameters.get(2), updateParameters.get(3));
						shouldUpdate.set(false);
					}
				}
			} catch (InterruptedException ignored) {
			}
		});
	}

	private void updateScreenDirectInternal(byte[] pixels, int x, int y, int width, int height) {
		if (pixels == null) {
			logger.warning("Direct pixel data is null");
			return;
		}
		Timing t = new Timing();
		GPURenderer.update(pixels, this.width, this.height, x, y, width, height);
		if (t.elapsedMS() > 60) //Typically 1ms max
			logger.warning("Direct update took " + t.elapsedMS() + "ms");
	}

	private void updateScreenIndirectInternal(ByteBuffer buffer, int x, int y, int width, int height) {
		if (buffer == null) {
			logger.warning("Indirect pixel buffer is null");
			return;
		}
		if (this.width * this.height > 640 * 480)
			buffer.limit(640 * 480 * 4);
		else
			buffer.limit(this.width * this.height * 4);
		Timing t = new Timing();
		BukkitRenderer.update(buffer, x, y, width, height);
		if (t.elapsedMS() > 60)
			logger.warning("Indirect update took " + t.elapsedMS() + "ms");
	}

	/**
	 * Updates the screen when the VM is embedded or when it isn't.
	 *
	 * @param x      The x of change - passed along to the renderer to use
	 * @param y      The y of change - passed along to the renderer to use
	 * @param width  The width of change - passed along to the renderer to use
	 * @param height The height of change - passed along to the renderer to use
	 */
	private void updateScreen(int x, int y, int width, int height) {
		if (direct) {
			val arr = embedded ? pointer.getByteArray(0L, this.width * this.height * 4) : screenImage;
			updateScreenDirectInternal(arr, x, y, width, height);
		} else {
			val bb = embedded ? pointer.getByteBuffer(0L, (long) this.width * this.height * 4) : screenBuffer;
			updateScreenIndirectInternal(bb, x, y, width, height);
		}
	}

	public void stop() {
		synchronized (this) {
			running = false;
			notifyAll();
		}
	}
}
