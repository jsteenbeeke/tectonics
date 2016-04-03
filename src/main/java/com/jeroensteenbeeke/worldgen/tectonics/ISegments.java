package com.jeroensteenbeeke.worldgen.tectonics;

public interface ISegments {
	long area();

	void reset();

	void reassign(long newarea, long tmps);

	void shift(long d_lft, long d_top);

	long size();

	ISegmentData get(long index);

	void add(ISegmentData... segments);

	long id(long index);

	void setId(long index, long id);

	long getContinentAt(int x, int y);
}
