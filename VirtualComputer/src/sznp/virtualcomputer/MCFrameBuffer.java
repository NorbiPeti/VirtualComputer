package sznp.virtualcomputer;

import org.virtualbox_5_1.IFramebuffer;
import org.virtualbox_5_1.ObjectRefManager;
import org.virtualbox_5_1.jaxws.VboxPortType;

public class MCFrameBuffer extends IFramebuffer {

	public MCFrameBuffer(String wrapped, ObjectRefManager objMgr, VboxPortType port) {
		super(wrapped, objMgr, port);
		// TODO Auto-generated constructor stub
	}

	/*@Override
	public nsISupports queryInterface(String arg0) {
		return null;
	}

	@Override
	public long getBitsPerPixel() {
		return 32;
	}

	@Override
	public long getBytesPerLine() {
		return 640 * 4;
	}

	@Override
	public long[] getCapabilities(long[] arg0) {
		return new long[] { FramebufferCapabilities.UpdateImage };
	}

	@Override
	public long getHeight() {
		return 480;
	}

	@Override
	public long getHeightReduction() {
		return 0;
	}

	@Override
	public IFramebufferOverlay getOverlay() {
		return null;
	}

	@Override
	public long getPixelFormat() {
		return BitmapFormat.RGBA;
	}

	private byte visibleRegionB;
	private long visibleRegionL;

	@Override
	public long getVisibleRegion(byte arg0, long arg1) {
		System.out.println("Visible region get.");
		return visibleRegionL;
	}

	@Override
	public long getWidth() {
		return 640;
	}

	@Override
	public long getWinId() {
		return 0;
	}

	@Override
	public void notify3DEvent(long arg0, byte[] arg1) {
	}

	@Override
	public void notifyChange(long arg0, long arg1, long arg2, long arg3, long arg4) {
	}

	@Override
	public void notifyUpdate(long arg0, long arg1, long arg2, long arg3) {
		// TODO Auto-generated method stub
		System.out.println("UPDATE");
	}

	@Override
	public void notifyUpdateImage(long arg0, long arg1, long arg2, long arg3, byte[] arg4) {
		// TODO Auto-generated method stub
		System.out.println("UPDATE IMAGE");
	}

	@Override
	public void processVHWACommand(byte arg0) {
	}

	@Override
	public void setVisibleRegion(byte arg0, long arg1) {
		visibleRegionB = arg0;
		visibleRegionL = arg1;
		System.out.println("Visible region set.");
	}

	@Override
	public boolean videoModeSupported(long arg0, long arg1, long arg2) {
		return true; // We support EVERYTHING
	}*/
}
