package sznp.virtualcomputer;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import jnr.ffi.LibraryLoader;
import org.bukkit.Color;
import org.bukkit.map.MapPalette;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Test {
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
		long t=System.nanoTime();
		int[] a= IntStream.range(0, 640*480).toArray();
		final int[] res=new int[a.length];
		Kernel kernel=new Kernel() {
			@Override
			public void run() {
				int i=getGlobalId();
				//System.out.println(i);
				//res[i]=a[i]+b[i];
				Color c=Color.fromBGR((int)a[i]);
				res[i]= MapPalette.matchColor(c.getRed(), c.getGreen(), c.getBlue());
			}
		};
		kernel.execute(Range.create(res.length));
		//System.out.println(Arrays.toString(res));
		System.out.println(a[10]);
		System.out.println("OpenCL time: "+(System.nanoTime()-t)/1000000f+"ms");

		t=System.nanoTime();
		for (int i = 0; i < res.length; i++) {
			Color c=Color.fromBGR((int)a[i]);
			res[i]= MapPalette.matchColor(c.getRed(), c.getGreen(), c.getBlue());
		}
		System.out.println("For loop time: "+(System.nanoTime()-t)/1000000f+"ms");

		t=System.nanoTime();
		System.arraycopy(a, 0, res, 0, res.length);
		System.out.println("Sys time: "+(System.nanoTime()-t)/1000000f+"ms");

		PXCLib pxc = LibraryLoader.create(PXCLib.class).search(new File("").getAbsolutePath()).load("pxc");
		ByteBuffer bb=ByteBuffer.allocateDirect(640*480);
		try {
			Field f=Buffer.class.getDeclaredField("address");
			f.setAccessible(true);
			long addr= (long) f.get(bb);
			pxc.setSource(addr, 640, 480, 5, 4);
			pxc.updateAndGetMap(0, 0, 640, 480, null);
		} catch (NoSuchFieldException | IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
