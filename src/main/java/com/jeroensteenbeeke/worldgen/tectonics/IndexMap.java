package com.jeroensteenbeeke.worldgen.tectonics;

public class IndexMap extends Matrix<Long> {

	public IndexMap(long width, long height) {
		super(width, height);
	}

	public IndexMap(IndexMap other) {
		super(other);
	}

}
