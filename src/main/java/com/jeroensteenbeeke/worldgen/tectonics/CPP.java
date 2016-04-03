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

	public static boolean eval(int expression) {
		return expression != 0;
	}

	public static boolean eval(long expression) {
		return expression != 0L;
	}

	public static int toInt(boolean expression) {
		return expression ? 1 : 0;
	}

	public static long toLong(boolean expression) {
		return expression ? 1L : 0L;
	}

	public static float toFloat(boolean expression) {
		return expression ? 1.0f : 0.0f;
	}
}
