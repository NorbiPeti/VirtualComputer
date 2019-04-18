package sznp.virtualcomputer;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.sun.jna.Pointer;
import jnr.ffi.LibraryLoader;
import org.bukkit.Color;
import org.bukkit.map.MapPalette;
import sznp.virtualcomputer.util.PXCLib;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.stream.IntStream;

public class Test {
	private int[] x = new int[]{10};

	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		/*System.out.println(new File("").getAbsolutePath());
		PXCLib pxc = LibraryLoader.create(PXCLib.class).search(new File("").getAbsolutePath()).load("pxc");
		ByteBuffer bb = ByteBuffer.allocateDirect(2);
		long[] x = new long[]{Pointer.nativeValue(Native.getDirectBufferPointer(bb))};
		pxc.convert(new int[]{5, 10}, x);
		System.out.println(bb.get(0)); //19 AYY //TO!DO: Use setSource, we don't want to wrap the native array*/
		//final int[] a={5,6,8,2,3,10,5,26,5,416,41,85,416,41};
		//final int[] b={10,80,10,3,32,20,56,85,51,968,156,5894,10,60,52};
		//final int[] a={5,6};
		//final int[] b={10,80};
		int[] a = IntStream.range(0, 640 * 480).toArray();
		final int[] res = new int[a.length];
		//java.awt.Color tc=new java.awt.Color(0);
		/*GPURenderer gr;
		try {
			gr=new GPURenderer((short) 0, null, 0, 0);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}*/
		//gr.pixels=new byte[1];
		int[] x = new Test().x;
		Kernel kernel=new Kernel() {
			@Override
			public void run() {
				int i=getGlobalId();
				//System.out.println(i);
				//res[i]=a[i]+b[i];
				//res[i]= MapPalette.matchColor(a[i] & 0x0000FF, a[i] & 0x00FF00 >> 8, a[i] & 0xFF0000 >> 16);
				//res[i] = a[i];
				//res[i]=tc.getBlue();
				//gr.pixels[0]=5;
				res[i] = x[0];
				//GPURenderer.px[0]=10;
				//GPURendererInternal.test[0]=10; - LAMBDAS
			}
		};
		long t = System.nanoTime();
		kernel.put(a).execute(Range.create(res.length)).get(res);
		//System.out.println(Arrays.toString(res));
		System.out.println(a[10]);
		System.out.println("OpenCL time: "+(System.nanoTime()-t)/1000000f+"ms");
		a[50] = 652; //Massive speedups after the fist pass
		t = System.nanoTime();
		kernel.put(a).execute(Range.create(res.length)).get(res);
		System.out.println(a[10]);
		System.out.println("Second OpenCL time: " + (System.nanoTime() - t) / 1000000f + "ms");

		t=System.nanoTime();
		for (int i = 0; i < res.length; i++) {
			Color c = Color.fromBGR(a[i]);
			res[i]= MapPalette.matchColor(c.getRed(), c.getGreen(), c.getBlue());
		}
		System.out.println("For loop time: "+(System.nanoTime()-t)/1000000f+"ms");

		t=System.nanoTime();
		System.arraycopy(a, 0, res, 0, res.length);
		System.out.println("Sys time: "+(System.nanoTime()-t)/1000000f+"ms");

		PXCLib pxc = LibraryLoader.create(PXCLib.class).search(new File("").getAbsolutePath()).load("pxc");
		ByteBuffer bb = ByteBuffer.allocateDirect(640 * 480 * 4);
		try {
			Field f=Buffer.class.getDeclaredField("address");
			f.setAccessible(true);
			long addr= (long) f.get(bb);
			pxc.setSource(addr, 640, 480, 5, 4);
			t = System.nanoTime();
			long p = pxc.updateAndGetMap(0, 0, 640, 480, null);
			if (p == 0) return;
			byte[] img = new Pointer(p).getByteArray(0, 128 * 128);
			System.out.println("img[50]: " + img[50]);
			System.out.println("Native time: " + (System.nanoTime() - t) / 1000000f + "ms");
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}

		kernel = new Kernel() {
			@Override
			public void run() {
				PXCLib pxc = LibraryLoader.create(PXCLib.class).search(new File("").getAbsolutePath()).load("pxc");
				pxc.updateAndGetMap(0, 0, 0, 0, null);
			}
		};
		kernel.execute(Range.create(1));
	}
}
