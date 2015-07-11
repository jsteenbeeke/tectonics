package com.jeroensteenbeeke.worldgen.tectonics;

import com.jeroensteenbeeke.hyperion.util.Asserts;

public class WorldPoint {
	private final long x;

	private final long y;

	public WorldPoint(long x, long y) {
		this.x = x;
		this.y = y;
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	public long toIndex(final WorldDimension dim) {
		return Asserts.numberVariable("y").withValue(y).atMost(dim.getHeight())
				* dim.getWidth()
				+ Asserts.numberVariable("x").withValue(x)
						.atMost(dim.getWidth());
	}

}
