package sznp.virtualcomputer;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import jnr.ffi.LibraryLoader;

import java.io.File;
import java.nio.ByteBuffer;

public class Test {
	public static void main(String[] args) {
		System.out.println(new File("").getAbsolutePath());
		PXCLib pxc = LibraryLoader.create(PXCLib.class).search(new File("").getAbsolutePath()).load("pxc");
		ByteBuffer bb = ByteBuffer.allocateDirect(2);
		long[] x = new long[]{Pointer.nativeValue(Native.getDirectBufferPointer(bb))};
		pxc.convert(new int[]{5, 10}, x);
		System.out.println(bb.get(0)); //19 AYY
	}
}
