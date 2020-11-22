package sznp.virtualcomputer.util;

public interface IMCFrameBuffer extends virtualcomputerwindows.IMCFrameBuffer {
	void notifyUpdate(long x, long y, long width, long height);

	void notifyUpdateImage(long x, long y, long width, long height, byte[] image);

	void notifyChange(long screenId, long xOrigin, long yOrigin, long width, long height);
}
