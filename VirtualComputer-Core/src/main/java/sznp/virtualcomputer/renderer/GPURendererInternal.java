package sznp.virtualcomputer.renderer;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.aparapi.device.Device;
import com.aparapi.internal.kernel.KernelManager;
import lombok.Getter;
import lombok.var;
import sznp.virtualcomputer.PluginMain;

//Accessing the GPURenderer results in ArrayIndexOutOfBoundsExceptions - IT'S THE LAMBDAS
public class GPURendererInternal extends Kernel {
	private int mapx;
	private int mapy;
	private int width;
	private int height;
	private byte[] pixels;
	//Can't use static fields because it leads to incorrect CL code
	private int[] colors;
	@SuppressWarnings({"FieldCanBeLocal", "unused"})
	private byte[] buffer; //References the map buffer
	private Range range;
	@Getter
	private boolean rendered;

	//public static byte[] test=new byte[1]; - LAMBDAS
	public GPURendererInternal(int mapx, int mapy, int[] colors) {
		this.mapx = mapx;
		this.mapy = mapy;
		this.colors = colors;
		//range = Range.create2D(128, 128);
		//var dev = KernelManager.instance().bestDevice();
		range = Range.create2D(128, 128);
		/*var dev = getTargetDevice();
		if (mapx == mapy && mapx == 0)
			PluginMain.Instance.getLogger().info("Using device: " + dev.getShortDescription());*/

		//Do an intial draw of a black screen with Aparapi so it doesn't lag at start
		pixels = new byte[1];
		width = height = 0;
		rendered = false;
	}

	@Override
	public void run() {
		//Single booleans not found (no such field error)
		int mx = getGlobalId(0);
		int my = getGlobalId(1);
		int imgx = mx + mapx * 128;
		int imgy = my + mapy * 128;
		int imgi = (imgy * width + imgx) * 4;
		if (imgx >= width || imgy >= height) { //Array length check --> Unhandled exception string
			buffer[my * 128 + mx] = matchColor(0, 0, 0);
			//buffer[my*128+mx]=10; - LAMBDAS
			return;
		}
		buffer[my * 128 + mx] = matchColor(pixels[imgi] & 0xFF, pixels[imgi + 1] & 0xFF, pixels[imgi + 2] & 0xFF); //Byte.toUnsignedInt
		//buffer[my*128+mx]=10; - LAMBDAS
		//Unhandled exception string (used & 255, after using toUnsignedInt on the blue color) - Not that, see above
	}

	//Modified version of MapPalette.matchColor
	private byte matchColor(int b, int g, int r) {
		int index = 0;
		double best = -1;

		for (int i = 4; i < colors.length; i++) {
			double distance = distance(b, g, r, colors[i]);
			if (distance < best || best == -1) {
				best = distance;
				index = i;
			}
		}

		// Minecraft has 143 colors, some of which have negative byte representations
		return (byte) (index < 128 ? index : -129 + (index - 127));
	}

	//Can't use getXY prefix because it treats it as a getter
	private double distance(int b1, int g1, int r1, int c2) {
		int red2 = color(c2, RED);
		double rmean = (r1 + red2) / 2.0;
		double r = r1 - red2;
		double g = g1 - color(c2, GREEN);
		int b = b1 - color(c2, BLUE);
		double weightR = 2 + rmean / 256.0;
		double weightG = 4.0;
		double weightB = 2 + (255 - rmean) / 256.0;
		return weightR * r * r + weightG * g * g + weightB * b * b;
	}

	private static int color(int bgra, int oc) {
		return (bgra >> (oc * 8)) & 0xFF;
	}

	private static final int RED = 2;
	private static final int GREEN = 1;
	private static final int BLUE = 0;

	//Aparapi fails with lambdas
	void setPixels(byte[] pixels, int width, int height) {
		this.pixels = pixels;
		this.width = width;
		this.height = height;
		rendered = false;
	}

	void render(byte[] buffer) {
		if (pixels == null || rendered) return;
		this.buffer = buffer;
		put(buffer).put(pixels).execute(range).get(buffer);
		rendered = true;
	}

	void ignoreChange() {
		rendered = true;
	}
}
