package com.jeroensteenbeeke.worldgen.tectonics;

public abstract class Point<N extends Number & Comparable<N>> {
	private final N x;

	private final N y;

	protected Point(N x, N y) {
		super();
		this.x = x;
		this.y = y;
	}

	public N getX() {
		return x;
	}

	public N getY() {
		return y;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((x == null) ? 0 : x.hashCode());
		result = prime * result + ((y == null) ? 0 : y.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point<?> other = (Point<?>) obj;
		if (x == null) {
			if (other.x != null)
				return false;
		} else if (!x.equals(other.x))
			return false;
		if (y == null) {
			if (other.y != null)
				return false;
		} else if (!y.equals(other.y))
			return false;
		return true;
	}

}