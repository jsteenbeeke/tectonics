package com.jeroensteenbeeke.worldgen.tectonics;

public interface ISegmentData {
	void incCollCount();

	void incArea();

	void enlarge_to_contain(long x, long y);

	void markNonExistent();

	void shift(long dx, long dy);
}
