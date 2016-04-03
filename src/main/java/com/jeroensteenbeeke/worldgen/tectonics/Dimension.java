package com.jeroensteenbeeke.worldgen.tectonics;

public class Dimension {
	protected final long width;

	protected final long height;

	public Dimension(long width, long height) {
		this.width = width;
		this.height = height;
	}

	public Dimension(Dimension original) {
		this(original.getWidth(), original.getHeight());
	}

	public long getWidth() {
		return width;
	}

	public long getHeight() {
		return height;
	}

	public long getArea() {
		return width * height;
	}

	public boolean contains(final long x, final long y) {
		return (x >= 0 && x < width && y >= 0 && y < height);
	}

	public boolean contains(final float x, final float y) {
		return (x >= 0 && x < width && y >= 0 && y < height);
	}

	public boolean contains(final FloatPoint p) {
		return (p.getX() >= 0 && p.getX() < width && p.getY() >= 0 && p.getY() < height);
	}

	public Dimension grow(long amountX, long amountY) {
		return new Dimension(width + amountX, height + amountY);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (height ^ (height >>> 32));
		result = prime * result + (int) (width ^ (width >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Dimension))
			return false;
		Dimension other = (Dimension) obj;
		if (height != other.height)
			return false;
		if (width != other.width)
			return false;
		return true;
	}

}