package sznp.virtualcomputer;

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
	 * @param mc Total count of maps used for the screen
	 */
	void setSource(long address, int w, int h, int mcx, int mcy);

	/**
	 * Updates map and returns it's content
	 * @return Full map data [mapc][data]
	 */
	byte[][] updateAndGetMap(int x, int y, int width, int height); //TODO: Only update parts that actually updated and return them per-map (flagDirty)
}
