package com.jeroensteenbeeke.worldgen.tectonics;

import java.util.Random;

public class Rand {
	public static float getFloat(Random source) {
		return source.nextFloat() - 0.5f;
	}

	public static int getInt(Random source) {
		return source.nextInt();
	}
}
