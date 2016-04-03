package com.jeroensteenbeeke.worldgen.tectonics;

import javax.annotation.CheckForNull;

import com.jeroensteenbeeke.hyperion.util.Asserts;

public abstract class Matrix<N extends Number> {
	private final N[] values;

	private final long width;

	private final long height;

	protected Matrix(final N[] values, final long width, final long height) {
		this.width = Asserts.numberParam("width").withValue(width).atLeast(0L);
		this.height = Asserts.numberParam("height").withValue(height)
				.atLeast(0L);

		this.values = values;
	}

	@SuppressWarnings("unchecked")
	protected Matrix(final long width, final long height) {
		this.width = Asserts.numberParam("width").withValue(width).atLeast(0L);
		this.height = Asserts.numberParam("height").withValue(height)
				.atLeast(0L);

		this.values = (N[]) new Object[(int) (width * height)];
	}

	@SuppressWarnings("unchecked")
	protected Matrix(Matrix<N> other) {
		this.width = other.getWidth();
		this.height = other.getHeight();

		this.values = (N[]) new Object[(int) (width * height)];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				set(x, y, other.get(x, y));
			}
		}
	}

	public long getHeight() {
		return height;
	}

	public long getWidth() {
		return width;
	}

	@CheckForNull
	public N get(long x, long y) {
		return values[(int) (Asserts.numberParam("x").withValue(x)
				.between(0L, width - 1) + width
				* Asserts.numberParam("y").withValue(y).between(0L, height - 1))];
	}

	public void setAll(N value) {
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				set(x, y, value);
			}
		}
	}

	public void set(long x, long y, N value) {
		values[(int) (Asserts.numberParam("x").withValue(x)
				.between(0L, width - 1) + width
				* Asserts.numberParam("y").withValue(y).between(0L, height - 1))] = value;
	}

	public long getArea() {
		return width * height;
	}

	public N[] getRawData() {
		return values;
	}

	public void setRaw(int i, N value) {
		values[i] = value;
	}

	public N getRaw(int i) {
		return values[i];
	}
}
