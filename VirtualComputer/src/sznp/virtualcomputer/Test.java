package sznp.virtualcomputer;

import com.aparapi.Kernel;
import com.aparapi.Range;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import jnr.ffi.LibraryLoader;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Arrays;

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
		final int[] a={5,6};
		final int[] b={10,80};
		final int[] res=new int[a.length];
		Kernel kernel=new Kernel() {
			@Override
			public void run() {
				int i=getGlobalId();
				//System.out.println(i);
				res[i]=a[i]+b[i];
			}
		};
		kernel.execute(Range.create(res.length));
		System.out.println(Arrays.toString(res));
	}
}
