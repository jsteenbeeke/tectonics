package com.jeroensteenbeeke.worldgen.tectonics;

import java.util.function.BiConsumer;

import com.jeroensteenbeeke.hyperion.util.Asserts;

public class Bounds {
	private final WorldDimension worldDimension;

	private FloatPoint position;

	private Dimension dimension;

	public Bounds(WorldDimension worldDimension, FloatPoint position,
			Dimension dimension) {
		this.worldDimension = worldDimension;
		this.position = position;
		this.dimension = dimension;

		Asserts.numberParam("dimension.width").withValue(dimension.getWidth())
				.atMost(worldDimension.getWidth());
		Asserts.numberParam("dimension.height")
				.withValue(dimension.getHeight())
				.atMost(worldDimension.getHeight());
	}

	private Rectangle asRect() {
		final long ilft = leftAsUint();
		final long itop = topAsUint();
		final long irgt = ilft + dimension.getWidth();
		final long ibtm = itop + dimension.getHeight();

		return new Rectangle(worldDimension, ilft, irgt, itop, ibtm);
	}

	public int index(long x, long y) {
		return (int) (Asserts.numberParam("y").withValue(y)
				.between(0L, dimension.getHeight() - 1)
				* dimension.getWidth() + Asserts.numberParam("x").withValue(x)
				.between(0L, dimension.getWidth() - 1));
	}

	public long getArea() {
		return dimension.getArea();
	}

	public long getWidth() {
		return dimension.getWidth();
	}

	public long getHeight() {
		return dimension.getHeight();
	}

	public long leftAsUint() {
		return Asserts.numberVariable("position.x")
				.withValue(position.getX().longValue()).atLeast(0L);
	}

	public long topAsUint() {
		return Asserts.numberVariable("position.y")
				.withValue(position.getY().longValue()).atLeast(0L);
	}

	public long rightAsUintNonInclusive() {
		return leftAsUint() + getWidth() - 1;
	}

	public long bottomAsUintNonInclusive() {

		return topAsUint() + getHeight() - 1;
	}

	public boolean containsWorldPoint(long x, long y) {
		return asRect().contains(x, y);
	}

	public boolean isInLimits(float x, float y) {
		if (x < 0)
			return false;
		if (y < 0)
			return false;
		long ux = (long) x;
		long uy = (long) y;
		return ux < dimension.getWidth() && uy < dimension.getHeight();
	}

	public void shift(float dx, float dy) {
		position.shift(dx, dy, worldDimension);
	}

	public void grow(int dx, int dy) {
		Asserts.numberParam("dx").withValue(dx).atLeast(0);
		Asserts.numberParam("dy").withValue(dy).atLeast(0);
		dimension.grow(dx, dy);
		Asserts.numberVariable("dimension.width")
				.withValue(dimension.getWidth())
				.atMost(worldDimension.getWidth());
		Asserts.numberVariable("dimension.height")
				.withValue(dimension.getHeight())
				.atMost(worldDimension.getHeight());
	}

	public long getValidMapIndex(long px, long py,
			BiConsumer<Long, Long> newValues) {
		long res = asRect().getMapIndex(px, py, newValues);
		if (res == Rectangle.BAD_INDEX) {
			throw new RuntimeException("BAD INDEX found");
		}
		return res;
	}

	public long getMapIndex(long px, long py, BiConsumer<Long, Long> newValues) {

		return asRect().getMapIndex(px, py, newValues);
	}
}
