package sznp.virtualcomputer.util;

public class Timing {
	private long start = System.nanoTime();

	public long elapsedMS() {
		return (System.nanoTime() - start) / 1000000L;
	}
}
