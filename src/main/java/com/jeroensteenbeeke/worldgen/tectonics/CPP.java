package com.jeroensteenbeeke.worldgen.tectonics;

/**
 * Conversion logic for converting C++ to Java constructs
 *
 */
public class CPP {
	public static int negate(int original) {
		if (original == 0) {
			return 1;
		} else {
			return 0;
		}
	}

	public static long negate(long original) {
		if (original == 0L) {
			return 1L;
		} else {
			return 0L;
		}
	}
}
