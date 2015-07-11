package com.jeroensteenbeeke.worldgen.tectonics;

import static com.jeroensteenbeeke.worldgen.tectonics.FloatMath.*;

import java.util.Random;

public class Noise {
	private static final float SQRDMD_ROUGHNESS = 0.35f;

	private static long nearest_pow(long num) {
		long n = 1;

		while (n < num) {
			n <<= 1;
		}

		return n;
	}

	public static void createSlowNoise(float[] map, WorldDimension tmpDim,
			Random randsource) {
		final long seed = randsource.nextLong();

		final long width = tmpDim.getWidth();

		final long height = tmpDim.getHeight();

		float persistence = 0.25f;

		float ka = 256 / seed;
		float kb = seed * 567 % 256;
		float kc = (seed * seed) % 256;
		float kd = (567 - seed) % 256;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				float fNX = x / (float) width; // we let the x-offset define the
												// circle
				float fNY = y / (float) height; // we let the x-offset define
												// the circle
				float fRdx = fNX * 2 * PI; // a full circle is two pi radians
				float fRdy = fNY * 4 * PI; // a full circle is two pi radians
				float fRdsSin = 1.0f;
				float noiseScale = 0.593f;
				float a = fRdsSin * sinf(fRdx);
				float b = fRdsSin * cosf(fRdx);
				float c = fRdsSin * sinf(fRdy);
				float d = fRdsSin * cosf(fRdy);
				float v = SimplexNoise.scaled_octave_noise_4d(4.0f,
						persistence, 0.25f, 0.0f, 1.0f, ka + a * noiseScale, kb
								+ b * noiseScale, kc + c * noiseScale, kd + d
								* noiseScale);
				if (map[(int) (y * width + x)] == 0.0f)
					map[(int) (y * width + x)] = v;
			}
		}
	}

	public static void createNoise(float[] map, WorldDimension tmpDim,
			Random randsource, boolean useSimplex) {
		if (useSimplex) {
			SimplexNoise.simplexnoise(randsource.nextLong(), map,
					tmpDim.getWidth(), tmpDim.getHeight(), SQRDMD_ROUGHNESS);
		} else {
			long side = tmpDim.getMax();
			side = nearest_pow(side) + 1;
			float[] squareTmp = new float[(int) (side * side)];

			for (int y = 0; y < tmpDim.getHeight(); y++) {
				for (int x = 0; x <= tmpDim.getWidth(); x++) {
					squareTmp[(int) (x + y * side)] = map[(int) (x + y
							* tmpDim.getWidth())];
				}
			}
			// to make it tileable we need to insert proper values in the
			// padding area
			// 1) on the right of the valid area
			for (int y = 0; y < tmpDim.getHeight(); y++) {
				for (long x = tmpDim.getWidth(); x < side; x++) {
					// we simply put it as a mix between the east and west
					// border (they should be fairly
					// similar because it is a toroidal world)
					squareTmp[(int) (y * side + x)] = (squareTmp[(int) (y
							* side + 0)] + squareTmp[(int) (y * side + (tmpDim
							.getWidth() - 1))]) / 2;
				}
			}
			// 2) below the valid area
			for (long y = tmpDim.getHeight(); y < side; y++) {
				for (int x = 0; x < side; x++) {
					// we simply put it as a mix between the north and south
					// border (they should be fairly
					// similar because it is a toroidal world)
					squareTmp[(int) (y * side + x)] = (squareTmp[(int) ((0) * side + x)] + squareTmp[(int) ((tmpDim
							.getHeight() - 1) * side + x)]) / 2;
				}
			}

			sqrdmd(randsource.nextLong(), squareTmp, side, SQRDMD_ROUGHNESS);

			// Calcuate deltas (noise introduced)
			float[] deltas = new float[(int) (tmpDim.getWidth() * tmpDim
					.getHeight())];
			for (int y = 0; y < tmpDim.getHeight(); y++) {
				for (int x = 0; x < tmpDim.getWidth(); x++) {
					deltas[(int) (y * tmpDim.getWidth() + x)] = squareTmp[(int) (y
							* side + x)]
							- map[(int) (y * tmpDim.getWidth() + x)];
				}
			}

			// make it tileable
			for (int y = 0; y < tmpDim.getHeight(); y++) {
				for (int x = 0; x < tmpDim.getWidth(); x++) {
					long specularX = tmpDim.getWidth() - 1 - x;
					long specularY = tmpDim.getHeight() - 1 - y;
					float myDelta = deltas[(int) (y * tmpDim.getWidth() + x)];
					float specularWidthDelta = deltas[(int) (y
							* tmpDim.getWidth() + specularX)];
					float specularHeightDelta = deltas[(int) (specularY
							* tmpDim.getWidth() + x)];
					float oppositeDelta = deltas[(int) (specularY
							* tmpDim.getWidth() + specularX)];
					map[(int) (y * tmpDim.getWidth() + x)] += (myDelta
							+ specularWidthDelta + specularHeightDelta + oppositeDelta) / 4;
				}
			}

		}
	}

	public static int sqrdmd(long seed, float[] map, long size, float rgh) {
		Random randsource = new Random(seed);

		final long full_size = size * size;

		int i;
		long temp;
		long x, y;
		long dx;
		long dy;
		long x0, x1, y0, y1;
		int p0;
		int p1;
		int p2, p3;
		long step;
		long line_jump, masked;
		float slope, sum, center_sum;
		i = 0;
		temp = size - 1;
		// MUST EQUAL TO 2^x + 1!
		if ((temp & (temp - 1) | temp & 3) == 0) {
			throw new IllegalArgumentException("Side should be 2**n +1");
		}
		temp = size;
		slope = rgh;
		step = size & ~1;

		/* Calculate midpoint ("diamond step"). */
		dy = step * size;

		sum = CALC_SUM(map[0], map[(int) step], map[(int) dy],
				map[(int) (dy + step)], randFloat(randsource), slope);
		SAVE_SUM(i, map, sum);
		center_sum = sum;

		/* Calculate each sub diamonds' center points ("square step"). */
		/* Top row. */
		p0 = (int) (step >> 1);

		SAVE_SUM(
				p0,
				map,
				CALC_SUM(map[0], map[(int) step], center_sum, center_sum,
						randFloat(randsource), slope));
		/* Left column. */
		p1 = (int) (p0 * size);

		SAVE_SUM(
				p1,
				map,
				CALC_SUM(map[0], map[(int) dy], center_sum, center_sum,
						randFloat(randsource), slope));
		map[(int) (full_size + p0 - size)] = map[(int) p0]; /*
															 * Copy top val into
															 * btm row.
															 */
		map[(int) (p1 + size - 1)] = map[(int) p1]; /*
													 * Copy left value into
													 * right column.
													 */
		slope *= rgh;
		step >>= 1;

		while (step > 1) /* Enter the main loop. */
		{
			/*************************************************************
			 * Calc midpoint of sub squares on the map ("diamond step"). *
			 *************************************************************/
			dx = step;
			dy = step * size;
			i = (int) ((step >> 1) * (size + 1));
			line_jump = step * size + 1 + step - size;
			for (y0 = 0, y1 = dy; y1 < size * size; y0 += dy, y1 += dy) {
				for (x0 = 0, x1 = dx; x1 < size; x0 += dx, x1 += dx, i += step) {
					sum = (map[(int) (y0 + x0)] + map[(int) (y0 + x1)]
							+ map[(int) (y1 + x0)] + map[(int) (y1 + x1)]) * 0.25f;
					sum = sum + slope * randFloat(randsource);
					masked = CPP.negate((int) map[(int) i]);
					map[(int) i] = map[(int) i] * CPP.negate(masked) + sum
							* masked;
				}
				/*
				 * There's additional step taken at the end of last valid loop.
				 * That step actually isn't valid because the row ends right
				 * then. Thus we are forced to manually remove it after the loop
				 * so that 'i' points again to the index accessed last.
				 */
				i += line_jump - step;
			}

			/**************************************************************
			 * Calculate each sub diamonds' center point ("square step").
			 * Diamond gets its left and right vertices from the square corners
			 * of last iteration and its top and bottom vertices from the
			 * "diamond step" we just performed.
			 *************************************************************/
			i = (int) (step >> 1);
			p0 = (int) step; /* right */
			p1 = (int) (i * size + i); /* bottom */
			p2 = 0; /* left */
			p3 = (int) (full_size + i - (i + 1) * size); /* top (wrapping edges) */

			/* Calculate "diamond" values for top row in map. */
			while (p0 < size) {
				sum = (map[p0] + map[p1] + map[p2] + map[p3]) * 0.25f;
				sum = sum + slope * randFloat(randsource);
				masked = CPP.negate((int) map[i]);
				map[i] = map[i] * CPP.negate(masked) + sum * masked;
				/* Copy it into bottom row. */
				map[(int) (full_size + i - size)] = map[i];
				p0 += step;
				p1 += step;
				p2 += step;
				p3 += step;
				i += step;
			}

			/*
			 * Now that top row's values are calculated starting from 'y = step
			 * >> 1' both saves us from recalculating same things twice and
			 * guarantees that data will not be read beyond top row of map.
			 * 'size - (step >> 1)' guarantees that data will not be read beyond
			 * bottom row of map.
			 */
			for (y = step >> 1, temp = 0; y < size - (step >> 1); y += step >> 1, temp = CPP
					.negate(temp)) {
				p0 = (int) (step >> 1); /* right */
				p1 = (int) (p0 * size); /* bottom */
				p2 = -p0; /* left */
				p3 = -p1; /* top */
				/* For even rows add step/2. Otherwise add nothing. */
				x = i = (int) (p0 * temp); /* Init 'x' while it's easy. */
				i += y * size; /* Move 'i' into correct row. */
				p0 += i;
				p1 += i;
				/* For odd rows p2 (left) wraps around map edges. */
				p2 += i + (size - 1) * CPP.negate(temp);
				p3 += i;
				/*
				 * size - (step >> 1) guarantees that data will not be read
				 * beyond rightmost column of map.
				 */
				for (; x < size - (step >> 1); x += step) {
					sum = (map[p0] + map[p1] + map[p2] + map[p3]) * 0.25f;
					sum = sum + slope * randFloat(randsource);
					masked = CPP.negate((int) map[i]);
					map[i] = map[i] * CPP.negate(masked) + sum * masked;
					p0 += step;
					p1 += step;
					p2 += step;
					p3 += step;
					i += step;
					/*
					 * if we start from leftmost column -> left point (p2) is
					 * going over the right border -> wrap it around into the
					 * beginning of previous rows left line.
					 */
					p2 -= (size - 1) * CPP.negate(x);
				}
				/* copy rows first element into its last */
				i = (int) (y * size);
				map[(int) (i + size - 1)] = map[i];
			}
			slope *= rgh; /* reduce amount of randomness for next round */
			step >>= 1; /* split squares and diamonds in half */
		}
		return (0);
	}

	private static float randFloat(Random randsource) {
		return randsource.nextFloat() - 0.5f;
	}

	private static float CALC_SUM(float a, float b, float c, float d,
			float rnd, float slope) {
		return (((a) + (b) + (c) + (d)) * 0.25f) + slope * rnd;
	}

	private static void SAVE_SUM(int a, float[] map, float sum) {
		boolean isZero = (int) map[a] == 0;
		if (isZero) {
			map[a] = sum;
		}
	}
}
