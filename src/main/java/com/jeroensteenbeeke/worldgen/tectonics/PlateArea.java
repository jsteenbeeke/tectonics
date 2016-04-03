package com.jeroensteenbeeke.worldgen.tectonics;

import java.util.List;

class PlateArea {
	private List<Long> border; // /< Plate's unprocessed border pixels.

	private long bottom; // /< Most bottom pixel of plate.

	private long left; // /< Most left pixel of plate.

	private long right; // /< Most right pixel of plate.

	private long top; // /< Most top pixel of plate.

	private long width; // /< Width of area in pixels.

	private long height; // /< Height of area in pixels.

	public List<Long> getBorder() {
		return border;
	}

	public void setBorder(List<Long> border) {
		this.border = border;
	}

	public long getBottom() {
		return bottom;
	}

	public void setBottom(long bottom) {
		this.bottom = bottom;
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

	public long getWidth() {
		return width;
	}

	public void setWidth(long width) {
		this.width = width;
	}

	public long getHeight() {
		return height;
	}

	public void setHeight(long height) {
		this.height = height;
	}

	public void incrementHeight() {
		this.height++;
	}

	public void incrementWidth() {
		this.width++;
	}

}
