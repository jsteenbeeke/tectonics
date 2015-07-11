package com.jeroensteenbeeke.worldgen.tectonics;

import com.jeroensteenbeeke.hyperion.util.Asserts;

public class WorldDimension extends Dimension {

	public WorldDimension(WorldDimension original) {
		super(original);
	}

	public WorldDimension(long width, long height) {
		super(width, height);
	}

	public long getMax() {
		return Math.max(getHeight(), getWidth());

	}

	public long xMod(int x) {
		return (safeX(x) + width) % getWidth();
	}

	public long yMod(int y) {
		return (safeY(y) + height) % getHeight();
	}

	public long xMod(long x) {
		return (safeX(x) + width) % getWidth();
	}

	public long yMod(long y) {
		return (safeY(y) + height) % getHeight();
	}

	public long indexOf(long x, long y) {
		return safeY(y) * width + safeX(x);
	}

	private Long safeX(long x) {
		return Asserts.numberParam("x").withValue(x).between(0L, width - 1);
	}

	private Long safeY(long y) {
		return Asserts.numberParam("y").withValue(y).between(0L, height - 1);
	}

	private Long safeIndex(long index) {
		return Asserts.numberParam("index").withValue(index)
				.between(0L, getArea() - 1);
	}

	public long lineIndex(long y) {
		return indexOf(0, safeY(y));
	}

	public long yFromIndex(long index) {
		return safeIndex(index) / width;
	}

	public long xFromIndex(long index) {

		final long y = yFromIndex(safeIndex(index));
		return index - y * width;
	}

	public long normalizedIndexOf(long x, long y) {
		return indexOf(xMod(x), yMod(y));
	}

	public long xCap(long x) {
		return x < width ? x : (width - 1);
	}

	public long yCap(long y) {

		return y < height ? y : (height - 1);
	}

	public long largerSize() {
		return Math.max(width, height);
	}
}
