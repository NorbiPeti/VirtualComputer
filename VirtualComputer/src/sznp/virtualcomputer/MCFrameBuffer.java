package sznp.virtualcomputer;

import org.mozilla.interfaces.IFramebuffer;
import org.mozilla.interfaces.IFramebufferOverlay;
import org.mozilla.interfaces.nsISupports;

public class MCFrameBuffer implements IFramebuffer {

	@Override
	public nsISupports queryInterface(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getBitsPerPixel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getBytesPerLine() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long[] getCapabilities(long[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getHeight() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getHeightReduction() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IFramebufferOverlay getOverlay() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getPixelFormat() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getVisibleRegion(byte arg0, long arg1) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getWinId() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void notify3DEvent(long arg0, byte[] arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyChange(long arg0, long arg1, long arg2, long arg3, long arg4) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyUpdate(long arg0, long arg1, long arg2, long arg3) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void notifyUpdateImage(long arg0, long arg1, long arg2, long arg3, byte[] arg4) {
		System.out.println("Update!");
	}

	@Override
	public void processVHWACommand(byte arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setVisibleRegion(byte arg0, long arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean videoModeSupported(long arg0, long arg1, long arg2) {
		// TODO Auto-generated method stub
		return false;
	}
}
