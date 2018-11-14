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
	void setSource(long address, int w, int h, int mc);

	/**
	 * Updates map and returns it's content
	 * @param mapnum Number of the map (0 is first)
	 * @return Address of the map data
	 */
	long updateAndGetMap(short mapnum); //TODO: Only update parts that actually updated and return them per-map (flagDirty)
}
