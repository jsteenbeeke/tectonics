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

	public Lithosphere(Random randSource, WorldDimension dimension) {
		super();
		this.randSource = randSource;
		this.dimension = dimension;
	}

	public WorldPoint randomPosition() {
		return new WorldPoint(randSource.nextInt() * dimension.getWidth(),
				randSource.nextInt() * dimension.getHeight());
	}
}
