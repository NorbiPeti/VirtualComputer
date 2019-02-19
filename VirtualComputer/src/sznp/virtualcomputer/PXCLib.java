package sznp.virtualcomputer;

import jnr.ffi.Pointer;

@SuppressWarnings("unused")
public interface PXCLib {
	/**
	 * Testing only
	 * @param px Input array
	 * @param out Output array
	 * @return 0
	 */
	int convert(int[] px, long[] out);

	/**
	 * Set source bitmap
	 * @param address Bitmap address from VirtualBox
	 * @param w Width of the screen
	 * @param h Height of the screen
	 * @param mcx Width of the screen in maps
	 * @param mcy Height of the screen in maps
	 */
	void setSource(long address, int w, int h, int mcx, int mcy);

	/**
	 * Updates map and returns it's content, where affected
	 * @return Partial map data [mapc][data]
	 */
	long updateAndGetMap(int x, int y, int width, int height, Pointer out_changed); //TODO: Only update parts that actually updated and return them per-map (flagDirty)
}
