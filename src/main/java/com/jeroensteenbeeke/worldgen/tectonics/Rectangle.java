package com.jeroensteenbeeke.worldgen.tectonics;

import java.util.function.BiConsumer;

import com.jeroensteenbeeke.hyperion.util.Asserts;

public class Rectangle {
	public final static int BAD_INDEX = 0xFFFFFFFF;

	private final WorldDimension worldDimension;

	private long left;

	private long right;

	private long top;

	private long bottom;

	public Rectangle(WorldDimension worldDimension, long left, long right,
			long top, long bottom) {
		this.worldDimension = worldDimension;
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
	}

	public Rectangle(Rectangle rectangle) {
		this.worldDimension = rectangle.getWorldDimension();
		this.bottom = rectangle.getBottom();
		this.left = rectangle.getLeft();
		this.right = rectangle.getRight();
		this.bottom = rectangle.getBottom();
	}

	public WorldDimension getWorldDimension() {
		return worldDimension;
	}

	public long getLeft() {
		return left;
	}

	public void setLeft(long left) {
		this.left = left;
	}

	public long getRight() {
		return right;
	}

	public void setRight(long right) {
		this.right = right;
	}

	public long getTop() {
		return top;
	}

	public void setTop(long top) {
		this.top = top;
	}

	public long getBottom() {
		return bottom;
	}

	public void setBottom(long bottom) {
		this.bottom = bottom;
	}

	public boolean contains(long x, long y) {
		long cleanX = worldDimension.xMod(x);
		long cleanY = worldDimension.yMod(y);
		if (cleanX < getLeft())
			cleanX += worldDimension.getWidth();
		if (cleanY < getTop())
			cleanY += worldDimension.getHeight();
		return cleanX >= getLeft() && cleanX < getRight() && cleanY >= getTop()
				&& cleanY < getBottom();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (bottom ^ (bottom >>> 32));
		result = prime * result + (int) (left ^ (left >>> 32));
		result = prime * result + (int) (right ^ (right >>> 32));
		result = prime * result + (int) (top ^ (top >>> 32));
		result = prime * result
				+ ((worldDimension == null) ? 0 : worldDimension.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Rectangle))
			return false;
		Rectangle other = (Rectangle) obj;
		if (bottom != other.bottom)
			return false;
		if (left != other.left)
			return false;
		if (right != other.right)
			return false;
		if (top != other.top)
			return false;
		if (worldDimension == null) {
			if (other.worldDimension != null)
				return false;
		} else if (!worldDimension.equals(other.worldDimension))
			return false;
		return true;
	}

	public long getMapIndex(long px, long py, BiConsumer<Long, Long> newValues) {
		long x = px % worldDimension.getWidth();
		long y = py % worldDimension.getHeight();

		final int ilft = (int) left;
		final int itop = (int) top;
		final int irgt = (int) right
				+ (((int) right < ilft) ? (int) worldDimension.getWidth() : 0);
		final int ibtm = (int) bottom
				+ (((int) bottom < itop) ? (int) worldDimension.getHeight() : 0);
		final int width = irgt - ilft;
		if (width < 0) {
			throw new IllegalArgumentException(
					"(Rectangle::getMapIndex) negative width");
		}

		// /////////////////////////////////////////////////////////////////////
		// If you think you're smart enough to optimize this then PREPARE to be
		// smart as HELL to debug it!
		// /////////////////////////////////////////////////////////////////////

		final int xOkA = CPP.toInt(x >= ilft) & CPP.toInt(x < irgt);
		final int xOkB = (int) ((x + CPP
				.toInt(worldDimension.getWidth() >= ilft)) & (x + CPP
				.toInt(worldDimension.getWidth() < irgt)));
		final int xOk = xOkA | xOkB;

		final int yOkA = CPP.toInt(y >= itop) & CPP.toInt(y < ibtm);
		final int yOkB = (int) ((y + CPP
				.toInt(worldDimension.getHeight() >= itop)) & (y + CPP
				.toInt(worldDimension.getHeight() < ibtm)));
		final int yOk = yOkA | yOkB;

		x += (x < ilft) ? worldDimension.getWidth() : 0; // Point is within
															// plate's map: wrap
		y += (y < itop) ? worldDimension.getHeight() : 0; // it around world
															// edges if
															// necessary.

		x -= ilft; // Calculate offset within local map.
		y -= itop;

		Asserts.numberVariable("x").withValue(x).atLeast(0L);
		Asserts.numberVariable("y").withValue(y).atLeast(0L);

		if (CPP.eval(xOk & yOk)) {
			newValues.accept(x, y);
			return (y * width + x);
		} else {
			return BAD_INDEX;
		}
	}

	public void enlarge_to_contain(long x, long y) {
		if (y < top) {
			top = y;
		} else if (y > bottom) {
			bottom = y;
		}
		if (x < left) {
			left = x;
		} else if (x > right) {
			right = x;
		}

	}

	public void shift(long dx, long dy) {
		left += dx;
		right += dx;
		top += dy;
		bottom += dy;
	}
}
