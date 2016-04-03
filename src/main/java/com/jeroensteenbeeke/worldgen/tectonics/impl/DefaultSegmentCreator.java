package com.jeroensteenbeeke.worldgen.tectonics.impl;

import java.util.ArrayList;
import java.util.List;

import com.jeroensteenbeeke.hyperion.util.Pointer;
import com.jeroensteenbeeke.worldgen.tectonics.*;

public class DefaultSegmentCreator implements ISegmentCreator {

	private final WorldDimension worldDimension;

	private final Bounds bounds;

	private final ISegments segments;

	private final HeightMap map;

	public DefaultSegmentCreator(WorldDimension worldDimension, Bounds bounds,
			ISegments segments, HeightMap map) {
		super();
		this.worldDimension = worldDimension;
		this.bounds = bounds;
		this.segments = segments;
		this.map = map;
	}

	@Override
	public long createSegment(long x, long y) {
		final long bounds_width = bounds.getWidth();
		final long bounds_height = bounds.getHeight();
		final int origin_index = bounds.index(x, y);
		final long ID = segments.size();

		if (segments.id(origin_index) < ID) {
			return segments.id(origin_index);
		}

		long nbour_id = calcDirection(x, y, origin_index, ID);

		if (nbour_id < ID) {
			segments.setId(origin_index, nbour_id);
			segments.get(nbour_id).incArea();
			segments.get(nbour_id).enlarge_to_contain(x, y);

			return nbour_id;
		}

		long lines_processed;
		Rectangle rect = new Rectangle(worldDimension, x, x, y, y);

		DefaultSegmentData pData = new DefaultSegmentData(rect, 0);
		List<List<Long>> spans_todo = new ArrayList<List<Long>>();
		List<List<Long>> spans_done = new ArrayList<List<Long>>();
		long spans_size = 0;
		// MK: This code was originally allocating the 2D arrays per function
		// call.
		// This was eating up a tremendous amount of cpu.
		// They are now static and they grow as needed, which turns out to be
		// seldom.
		if (spans_size < bounds_height) {
			spans_todo = new ArrayList<List<Long>>((int) bounds_height);
			spans_done = new ArrayList<List<Long>>((int) bounds_height);
			spans_size = bounds_height;
		}
		segments.setId(origin_index, ID);
		spans_todo.get((int) y).add(x);
		spans_todo.get((int) y).add(x);

		do {
			lines_processed = 0;
			for (int line = 0; line < bounds_height; ++line) {
				Pointer<Long> start = Pointer.of(0L);
				Pointer<Long> end = Pointer.of(0L);

				if (spans_todo.get((int) line).isEmpty())
					continue;

				scanSpans(line, start, end, spans_todo, spans_done);

				if (start.get() > end.get()) // Nothing to do here anymore...
					continue;

				// Calculate line indices. Allow wrapping around map edges.
				final long row_above = ((line - 1) & -(line > 0 ? 1 : 0))
						| ((bounds_height - 1) & -(line == 0 ? 1 : 0));
				final long row_below = (line + 1)
						& -(line < bounds_height - 1 ? 1 : 0);
				final long line_here = line * bounds_width;
				final long line_above = row_above * bounds_width;
				final long line_below = row_below * bounds_width;

				// Extend the beginning of line.
				while (start.get() > 0
						&& segments.id(line_here + start.get() - 1) > ID
						&& map.getRaw((int) (line_here + start.get() - 1)) >= Const.CONT_BASE) {
					start.set(start.get() - 1);
					segments.setId(line_here + start.get(), ID);

					// Count volume of pixel...
				}

				// Extend the end of line.
				while (end.get() < bounds_width - 1
						&& segments.id(line_here + end.get() + 1) > ID
						&& map.getRaw((int) (line_here + end.get() + 1)) >= Const.CONT_BASE) {
					end.set(end.get() + 1);
					segments.setId(line_here + end.get(), ID);

					// Count volume of pixel...
				}

				// Check if should wrap around left edge.
				if (bounds_width == worldDimension.getWidth()
						&& start.get() == 0L
						&& segments.id(line_here + bounds_width - 1) > ID
						&& map.getRaw((int) (line_here + bounds_width - 1)) >= Const.CONT_BASE) {
					segments.setId(line_here + bounds_width - 1, ID);
					spans_todo.get((int) line).add(bounds_width - 1);
					spans_todo.get((int) line).add(bounds_width - 1);

					// Count volume of pixel...
				}

				// Check if should wrap around right edge.
				if (bounds_width == worldDimension.getWidth()
						&& end.get() == bounds_width - 1
						&& segments.id(line_here + 0) > ID
						&& map.getRaw((int) (line_here + 0)) >= Const.CONT_BASE) {
					segments.setId(line_here + 0, ID);
					spans_todo.get((int) line).add(0L);
					spans_todo.get((int) line).add(0L);

					// Count volume of pixel...
				}

				pData.incArea(1 + end.get() - start.get()); // Update segment
															// area counter.

				// Record any changes in extreme dimensions.
				if (line < pData.getTop())
					pData.setTop(line);
				if (line > pData.getBottom())
					pData.setBottom(line);
				if (start.get() < pData.getLeft())
					pData.setLeft(start.get());
				if (end.get() > pData.getRight())
					pData.setRight(end.get());

				if (line > 0 || bounds_height == worldDimension.getHeight()) {
					for (long j = start.get(); j <= end.get(); ++j)
						if (segments.id(line_above + j) > ID
								&& map.getRaw((int) (line_above + j)) >= Const.CONT_BASE) {
							long a = j;
							segments.setId(line_above + a, ID);

							// Count volume of pixel...

							while (++j < bounds_width
									&& segments.id(line_above + j) > ID
									&& map.getRaw((int) (line_above + j)) >= Const.CONT_BASE) {
								segments.setId(line_above + j, ID);

								// Count volume of pixel...
							}

							long b = --j; // Last point is invalid.

							spans_todo.get((int) row_above).add(a);
							spans_todo.get((int) row_above).add(b);
							++j; // Skip the last scanned point.
						}
				}

				if (line < bounds_height - 1
						|| bounds_height == worldDimension.getHeight()) {
					for (long j = start.get(); j <= end.get(); ++j)
						if (segments.id(line_below + j) > ID
								&& map.getRaw((int) (line_below + j)) >= Const.CONT_BASE) {
							long a = j;
							segments.setId(line_below + a, ID);

							// Count volume of pixel...

							while (++j < bounds_width
									&& segments.id(line_below + j) > ID
									&& map.getRaw((int) (line_below + j)) >= Const.CONT_BASE) {
								segments.setId(line_below + j, ID);

								// Count volume of pixel...
							}

							long b = --j; // Last point is invalid.

							spans_todo.get((int) row_below).add(a);
							spans_todo.get((int) row_below).add(b);
							++j; // Skip the last scanned point.
						}
				}

				spans_done.get((int) line).add(start.get());
				spans_done.get((int) line).add(end.get());
				++lines_processed;
			}
		} while (lines_processed > 0);

		for (long line = 0; line < bounds_height; line++) {
			spans_todo.get((int) line).clear();
			spans_done.get((int) line).clear();
		}
		segments.add(pData);

		return ID;
	}

	private long calcDirection(long x, long y, int origin_index, long ID) {
		boolean canGoLeft = x > 0
				&& map.getRaw(origin_index - 1) >= Const.CONT_BASE;
		boolean canGoRight = x < bounds.getWidth() - 1
				&& map.getRaw(origin_index + 1) >= Const.CONT_BASE;
		boolean canGoUp = y > 0
				&& map.getRaw((int) (origin_index - bounds.getWidth())) >= Const.CONT_BASE;
		boolean canGoDown = y < bounds.getHeight() - 1
				&& map.getRaw((int) (origin_index + bounds.getWidth())) >= Const.CONT_BASE;
		long nbour_id = ID;

		// This point belongs to no segment yet.
		// However it might be a neighbour to some segment created earlier.
		// If such neighbour is found, associate this point with it.
		if (canGoLeft && segments.id(origin_index - 1) < ID) {
			nbour_id = segments.id(origin_index - 1);
		} else if (canGoRight && segments.id(origin_index + 1) < ID) {
			nbour_id = segments.id(origin_index + 1);
		} else if (canGoUp
				&& segments.id(origin_index - bounds.getWidth()) < ID) {
			nbour_id = segments.id(origin_index - bounds.getWidth());
		} else if (canGoDown
				&& segments.id(origin_index + bounds.getWidth()) < ID) {
			nbour_id = segments.id(origin_index + bounds.getWidth());
		}

		return nbour_id;
	}

	private void scanSpans(int line, Pointer<Long> start, Pointer<Long> end,
			List<List<Long>> spans_todo, List<List<Long>> spans_done) {
		do // Find an unscanned span on this line.
		{
			end.set(spans_todo.get(line).remove(spans_todo.size() - 1));
			start.set(spans_todo.get(line).remove(spans_todo.size() - 1));

			// Reduce any done spans from this span.
			for (long j = 0; j < spans_done.get(line).size(); j += 2) {
				// Saved coordinates are AT the point
				// that was included last to the span.
				// That's why equalities matter.

				if (start.get() >= spans_done.get(line).get((int) j)
						&& start.get() <= spans_done.get(line).get((int) j + 1))
					start.set(spans_done.get(line).get((int) j + 1) + 1);

				if (end.get() >= spans_done.get(line).get((int) j)
						&& end.get() <= spans_done.get(line).get((int) (j + 1)))
					end.set(spans_done.get(line).get((int) j) - 1);
			}

			// Unsigned-ness hacking!
			// Required to fix the underflow of end - 1.
			start.set(start.get() | -(end.get() >= bounds.getWidth() ? 1 : 0));
			end.set(end.get() - (end.get() >= bounds.getWidth() ? 1 : 0));

		} while (start.get() > end.get() && spans_todo.get(line).size() > 0);
	}

}
