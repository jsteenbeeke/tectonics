package com.jeroensteenbeeke.worldgen.tectonics;

public class HeightMap extends Matrix<Float> {

	public HeightMap(long width, long height) {
		super(width, height);
	}

	public HeightMap(float[] values, long width, long height) {
		super(box(values), width, height);
	}

	public HeightMap(HeightMap other) {
		super(other);
	}

	private static Float[] box(float[] values) {
		Float[] boxed = new Float[values.length];
		for (int i = 0; i < values.length; i++) {
			boxed[i] = values[i];
		}

		return boxed;
	}

}
