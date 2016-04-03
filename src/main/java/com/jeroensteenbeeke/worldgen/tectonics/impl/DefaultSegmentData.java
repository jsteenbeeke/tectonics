package com.jeroensteenbeeke.worldgen.tectonics.impl;

import com.jeroensteenbeeke.worldgen.tectonics.ISegmentData;
import com.jeroensteenbeeke.worldgen.tectonics.Rectangle;

public class DefaultSegmentData implements ISegmentData {
	private final Rectangle rectangle;

	private long area;

	private long coll_count;

	public DefaultSegmentData(Rectangle rectangle, long area) {
		this.rectangle = rectangle;
		this.area = area;
		this.coll_count = 0;
	}

	@Override
	public void incCollCount() {
		coll_count++;
	}

	@Override
	public void incArea() {
		area++;
	}

	@Override
	public void enlarge_to_contain(long x, long y) {
		rectangle.enlarge_to_contain(x, y);

	}

	@Override
	public void markNonExistent() {
		area = 0;
	}

	@Override
	public void shift(long dx, long dy) {
		rectangle.shift(dx, dy);
	}

	public long getLeft() {
		return rectangle.getLeft();
	}

	public long getRight() {
		return rectangle.getRight();
	}

	public long getTop() {
		return rectangle.getTop();
	}

	public long getBottom() {
		return rectangle.getBottom();
	}

	public void setLeft(long left) {
		rectangle.setLeft(left);
	}

	public void setRight(long right) {
		rectangle.setRight(right);
	}

	public void setTop(long top) {
		rectangle.setTop(top);
	}

	public void setBottom(long bottom) {
		rectangle.setBottom(bottom);
	}

	public boolean isEmpty() {
		return area == 0;
	}

	public void incArea(long amount) {
		area += amount;
	}

	public long area() {
		return area;
	}

	public long collCount() {
		return coll_count;
	}

}
