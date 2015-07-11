package com.jeroensteenbeeke.worldgen.tectonics;

import java.util.List;
import java.util.Random;

public class Lithosphere {
	private static final float SUBDUCT_RATIO = 0.5f;

	private static final float BUOYANCY_BONUS_X = 3;
	private static final long MAX_BUOYANCY_AGE = 20;
	private static final float MULINV_MAX_BUOYANCY_AGE = 1.0f / (float) MAX_BUOYANCY_AGE;

	private static final float RESTART_ENERGY_RATIO = 0.15f;
	private static final float RESTART_SPEED_LIMIT = 2.0f;

	private static final long NO_COLLISION_TIME_LIMIT = 10;

	public static long findBound(final List<Long> map, long length, long x0,
			long y0, int dx, int dy) {
		throw new UnsupportedOperationException("Not yet implemented");
		// TODO
	}

	public static long findPlate(List<Plate> plates, float x, float y,
			long num_plates) {
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

	public Lithosphere(long seed, long width, long height, float sea_level,
			long _erosion_period, float _folding_ratio, long aggr_ratio_abs,
			float aggr_ratio_rel, long num_cycles) {
		super();
		this.randSource = new Random(seed);
		this.dimension = new WorldDimension(width, height);
		this.ageMap = new AgeMap(width, height);

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

	private void growPlates(PlateArea area, IndexMap owner) {

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
