package com.jeroensteenbeeke.worldgen.tectonics;

public class AgeMap extends Matrix<Long> {

	public AgeMap(long width, long height) {
		super(width, height);
	}

	public AgeMap(AgeMap other) {
		super(other);
	}

}
