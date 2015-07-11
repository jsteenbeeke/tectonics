package com.jeroensteenbeeke.worldgen.tectonics;

public class HeightMap extends Matrix<Float> {

	public HeightMap(long width, long height) {
		super(width, height);
	}

	public HeightMap(HeightMap other) {
		super(other);
	}

}
