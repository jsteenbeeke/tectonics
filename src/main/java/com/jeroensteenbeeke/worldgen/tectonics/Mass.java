package com.jeroensteenbeeke.worldgen.tectonics;

import com.jeroensteenbeeke.hyperion.util.Asserts;

public class Mass {
	public static class Builder {
		private float mass;

		private float cx;

		private float cy;

		public Builder(final float[] m, final Dimension dimension) {
			int k;
			for (int y = k = 0; y < dimension.getHeight(); ++y) {
				for (int x = 0; x < dimension.getWidth(); ++x, ++k) {
					Asserts.numberVariable("m[k]").withValue(m[k])
							.atLeast(0.0f);
					addPoint(x, y, m[k]);
				}
			}
		}

		private void addPoint(int x, int y, float crust) {
			Asserts.numberVariable("crust").withValue(crust).atLeast(0.0f);
			mass += crust;
			// Update the center coordinates weighted by mass.
			cx += x * crust;
			cy += y * crust;
		}

		public Mass build() {
			return new Mass(mass, cx, cy);
		}
	}

	private float mass;

	private float cx;

	private float cy;

	public Mass(float mass, float cx, float cy) {
		this.mass = mass;
		this.cx = cx;
		this.cy = cy;
	}

	public void incMass(float delta) {
		mass += delta;
		if (mass < 0.0f && mass > -0.01f) {
			mass = 0.0f;
		}

		Asserts.numberVariable("mass").withValue(mass).atLeast(0.0f);
	}

	public float getCx() {
		return cx;
	}

	public float getCy() {
		return cy;
	}

	public float getMass() {
		return mass;
	}

	public boolean isNull() {
		return mass <= 0f;
	}

}
