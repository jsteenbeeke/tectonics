package com.jeroensteenbeeke.worldgen.tectonics;

public class WorldPoint {
	private final int x;

	private final int y;

	public WorldPoint(int x, int y) {
		super();
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int toIndex(final WorldDimension dim) {
		if (x >= dim.getWidth()) {
			throw new RuntimeException("Worldpoint X outside of bounds");
		}
		if (y >= dim.getHeight()) {
			throw new RuntimeException("WorldpointY  outside of bounds");
		}
		return y * dim.getWidth() + x;
	}

}
