package com.jeroensteenbeeke.worldgen.tectonics;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.google.common.collect.Lists;
import com.jeroensteenbeeke.hyperion.util.Asserts;

public class Lithosphere {
	private static final float SUBDUCT_RATIO = 0.5f;

	private static final float BUOYANCY_BONUS_X = 3;

	private static final long MAX_BUOYANCY_AGE = 20;

	private static final float MULINV_MAX_BUOYANCY_AGE = 1.0f / (float) MAX_BUOYANCY_AGE;

	private static final float RESTART_ENERGY_RATIO = 0.15f;

	private static final float RESTART_SPEED_LIMIT = 2.0f;

	private static final long NO_COLLISION_TIME_LIMIT = 10;

	private static final float CONTINENTAL_BASE = 1.0f;

	private static final float OCEANIC_BASE = 0.1f;

	public static long findBound(final List<Long> map, long length, long x0,
			long y0, int dx, int dy) {
		throw new UnsupportedOperationException("Not yet implemented");
		// TODO
	}

	public static long findPlate(List<Plate> plates, float x, float y,
			long numPlates) {
		throw new UnsupportedOperationException("Not yet implemented");
		// TODO
	}

	private final Random randSource;

	private final WorldDimension dimension;
	/**
	 * Height map representing the topography of system.
	 */
	private HeightMap heightMap;
	/**
	 * Plate index map of the "owner" of each map point.
	 */
	private IndexMap indexMap;
	/**
	 * Age map of the system's surface (topography).
	 */
	private AgeMap ageMap;
	/**
	 * Array of plates that constitute the system.
	 */
	private List<Plate> plates;

	/**
	 * # of overlapping pixels -> aggregation.
	 */
	private long aggregationOverlapAbsolute;
	/**
	 * % of overlapping area -> aggregation.
	 */
	private float aggregationOverlapRelative;
	/**
	 * Number of times the system's been restarted.
	 */
	private long cycleCount;
	/**
	 * # of iterations between global erosion.
	 */
	private long erosionPeriod;
	/**
	 * Percent of overlapping crust that's folded.
	 */
	private float foldingRatio;
	/**
	 * Iteration count. Used to timestamp new crust.
	 */
	private long iterCount;
	/**
	 * Max number of times the system'll be restarted.
	 */
	private long maxCycles;
	/**
	 * Number of plates in the initial setting.
	 */
	private long maxPlates;
	/**
	 * Number of plates in the current setting.
	 */
	private long numPlates;

	private List<List<PlateCollision>> collisions;

	private List<List<PlateCollision>> subductions;

	/**
	 * Max total kinetic energy in the system so far.
	 */
	private float peakKineticEnergy;

	/**
	 * Iterations since last cont. collision.
	 */
	private long lastCollisionCount;

	private int steps;

	private float seaLevel;

	public Lithosphere(long seed, long width, long height, float sea_level,
			long erosion_period, float folding_ratio, long aggr_ratio_abs,
			float aggr_ratio_rel, long num_cycles) {
		super();
		this.randSource = new Random(seed);
		this.dimension = new WorldDimension(Asserts.numberParam("width")
				.withValue(width).atLeast(5L), Asserts.numberParam("height")
				.withValue(height).atLeast(5L));
		this.ageMap = new AgeMap(width, height);
		this.plates = Lists.newArrayList();
		this.aggregationOverlapAbsolute = aggr_ratio_abs;
		this.aggregationOverlapRelative = aggr_ratio_rel;
		this.cycleCount = 0;
		this.erosionPeriod = erosion_period;
		this.foldingRatio = folding_ratio;
		this.iterCount = 0;
		this.maxCycles = num_cycles;
		this.maxPlates = 0;
		this.numPlates = 0;
		this.steps = 0;
		this.seaLevel = sea_level;

		init();
	}

	private void init() {
		final long A = dimension.getArea();
		float[] tmp = new float[(int) A];

		createSlowNoise(tmp, dimension);

		float lowest = tmp[0], highest = tmp[0];
		for (int i = 1; i < A; ++i) {
			lowest = lowest < tmp[i] ? lowest : tmp[i];
			highest = highest > tmp[i] ? highest : tmp[i];
		}

		for (int i = 0; i < A; ++i)
			// Scale to [0 ... 1]
			tmp[i] = (tmp[i] - lowest) / (highest - lowest);

		float sea_threshold = 0.5f;
		float th_step = 0.5f;

		// Find the actual value in height map that produces the continent-sea
		// ratio defined be "sea_level".
		while (th_step > 0.01) {
			float count = 0;
			for (int i = 0; i < A; ++i) {
				count += CPP.toFloat(tmp[i] < sea_threshold);
			}

			th_step *= 0.5;
			if (count / (float) A < seaLevel)
				sea_threshold += th_step;
			else
				sea_threshold -= th_step;
		}

		seaLevel = sea_threshold;
		for (int i = 0; i < A; ++i) // Genesis 1:9-10.
		{
			tmp[i] = CPP.toFloat(tmp[i] > seaLevel)
					* (tmp[i] + CONTINENTAL_BASE)
					+ CPP.toFloat(tmp[i] <= seaLevel) * OCEANIC_BASE;
		}

		// Scalp the +1 away from map side to get a power of two side length!
		// Practically only the redundant map edges become removed.
		for (int y = 0; y < dimension.getHeight(); ++y) {
			int sourcePosition = dimension.lineIndex(y);
			Float[] data = heightMap.getRawData();

			System.arraycopy(tmp, sourcePosition, data, sourcePosition,
					(int) dimension.getWidth() * Float.BYTES);

		}

	}

	public void createPlates(int numPlates) {
		final long map_area = dimension.getArea();
		this.maxPlates = this.numPlates = numPlates;

		LinkedList<PlateCollision> vec = Lists.newLinkedList();

		for (int i = 0; i < numPlates; ++i) {
			collisions.add(vec);
			subductions.add(vec);
		}

		// Initialize "Free plate center position" lookup table.
		// This way two plate centers will never be identical.
		for (int i = 0; i < map_area; ++i) {
			indexMap.setRaw(i, (long) i);
		}

		// Select N plate centers from the global map.
		PlateArea[] area = new PlateArea[numPlates];
		for (int i = 0; i < numPlates; ++i) {
			// Randomly select an unused plate origin.
			final long p = indexMap
					.getRaw((int) (Rand.getInt(randSource) % (map_area - 1)));

			final long y = dimension.yFromIndex(p);
			final long x = dimension.xFromIndex(p);

			area[i].setLeft(x);
			area[i].setRight(x);
			area[i].setTop(y);
			area[i].setBottom(y);
			area[i].setWidth(1);
			area[i].setHeight(1);

			area[i].getBorder().add(p); // ...and mark it as border.

			// Overwrite used entry with last unused entry in array.
			indexMap.setRaw((int) p, indexMap.getRaw((int) (map_area - i - 1)));
		}

		indexMap.setAll((long) 0xFFFFFFFF);

		growPlates(area, indexMap);

		// check all the points of the map are owned
		for (int i = 0; i < indexMap.getArea(); i++) {
			if (indexMap.getRaw(i) < numPlates) {
				throw new IllegalStateException(
						"A point was not assigned to any plate");
			}
		}

		plates = Lists.newArrayListWithCapacity(numPlates);

		// Extract and create plates from initial terrain.
		for (int i = 0; i < numPlates; ++i) {
			area[i].setWidth(dimension.xCap(area[i].getWidth()));
			area[i].setHeight(dimension.yCap(area[i].getHeight()));

			final long x0 = area[i].getLeft();
			final long x1 = 1 + x0 + area[i].getWidth();
			final long y0 = area[i].getTop();
			final long y1 = 1 + y0 + area[i].getHeight();
			final long width = x1 - x0;
			final long height = y1 - y0;
			float[] plt = new float[(int) (width * height)];

			// Copy plate's height data from global map into local map.
			for (long y = y0, j = 0; y < y1; ++y) {
				for (long x = x0; x < x1; ++x, ++j) {
					long k = dimension.normalizedIndexOf(x, y);
					plt[(int) j] = heightMap.getRaw((int) k)
							* CPP.toFloat(indexMap.getRaw((int) k) == i);
				}
			}

			// Create plate.
			plates.set(i, new Plate(Rand.getInt(randSource), plt, width,
					height, x0, y0, i, dimension));
		}

		iterCount = numPlates + MAX_BUOYANCY_AGE;
		peakKineticEnergy = 0;
		lastCollisionCount = 0;
	}

	public WorldPoint randomPosition() {
		return new WorldPoint(randSource.nextInt() * dimension.getWidth(),
				randSource.nextInt() * dimension.getHeight());
	}

	private void createNoise(float[] tmp, WorldDimension tmpDim) {
		createNoise(tmp, tmpDim, false);
	}

	private void createNoise(float[] tmp, WorldDimension tmpDim,
			boolean useSimplex) {
		Noise.createNoise(tmp, tmpDim, randSource, useSimplex);
	}

	private void createSlowNoise(float[] tmp, WorldDimension tmpDim) {

	}

	private void updateHeightAndPlateIndexMaps(long map_area,
			long oceanic_collisions, long continental_collisions) {

	}

	private void updateCollisions() {

	}

	private void growPlates(PlateArea[] area, IndexMap owner) {
		// "Grow" plates from their origins until surface is fully populated.
		int max_border = 1;
		int i;
		while (CPP.eval(max_border)) {
			for (max_border = i = 0; i < numPlates; ++i) {
				final int N = area[i].getBorder().size();
				max_border = max_border > N ? max_border : N;

				if (N == 0)
					continue;

				final int j = Rand.getInt(randSource) % N;
				final long p = area[i].getBorder().get(j);
				final long cy = dimension.yFromIndex(p);
				final long cx = dimension.xFromIndex(p);

				final long lft = cx > 0 ? cx - 1 : dimension.getWidth() - 1;
				final long rgt = cx < dimension.getWidth() - 1 ? cx + 1 : 0;
				final long top = cy > 0 ? cy - 1 : dimension.getHeight() - 1;
				final long btm = cy < dimension.getHeight() - 1 ? cy + 1 : 0;

				final int n = (int) (top * dimension.getWidth() + cx); // North.
				final int s = (int) (btm * dimension.getWidth() + cx); // South.
				final int w = (int) (cy * dimension.getWidth() + lft); // West.
				final int e = (int) (cy * dimension.getWidth() + rgt); // East.

				if (owner.getRawData()[n] >= numPlates) {
					owner.getRawData()[n] = (long) i;
					area[i].getBorder().add((long) n);

					if (area[i].getTop() == dimension.yMod(top + 1)) {
						area[i].setTop(top);
						area[i].incrementHeight();
					}
				}

				if (owner.getRawData()[s] >= numPlates) {
					owner.getRawData()[s] = (long) i;
					area[i].getBorder().add((long) s);

					if (btm == dimension.yMod(area[i].getBottom() + 1)) {
						area[i].setBottom(btm);
						area[i].incrementHeight();
					}
				}

				if (owner.getRawData()[w] >= numPlates) {
					owner.getRawData()[w] = (long) i;
					area[i].getBorder().add((long) w);

					if (area[i].getLeft() == dimension.xMod(lft + 1)) {
						area[i].setLeft(lft);
						area[i].incrementWidth();
					}
				}

				if (owner.getRawData()[e] >= numPlates) {
					owner.getRawData()[e] = (long) i;
					area[i].getBorder().add((long) e);

					if (rgt == dimension.xMod(area[i].getRight() + 1)) {
						area[i].setRight(rgt);
						area[i].incrementWidth();
					}
				}

				// Overwrite processed point with unprocessed one.
				int lastIndex = area[i].getBorder().size() - 1;
				area[i].getBorder().set(j, area[i].getBorder().get(lastIndex));

				area[i].getBorder().remove(lastIndex);
			}
		}
	}

	private void removeEmptyPlates(long[] indexFound) {

	}

	private void resolveJuxtapositions(long i, long j, long k, long x_mod,
			long y_mod, float[] this_map, long[] this_age,
			long continental_collisions) {

	}

	private void restart() {

	}

	private static class PlateCollision {
		private final long index;

		private final long wx;

		private final long wy;

		private final float crust;

		public PlateCollision(long index, long x, long y, float z) {
			this.index = index;
			this.wx = x;
			this.wy = y;
			this.crust = z;
		}

		public long getIndex() {
			return index;
		}

		public long getWx() {
			return wx;
		}

		public long getWy() {
			return wy;
		}

		public float getCrust() {
			return crust;
		}

	}
}
