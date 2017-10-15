package sznp.virtualcomputer;

import com.sun.jna.Library;
import com.sun.jna.Native;

public interface WinLib extends Library {
	WinLib INSTANCE = Native.loadLibrary("VirtualComputerWin", WinLib.class);

	public void init();
}
