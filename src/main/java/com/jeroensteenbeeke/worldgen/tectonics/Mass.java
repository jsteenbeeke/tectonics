package com.jeroensteenbeeke.worldgen.tectonics;

import com.jeroensteenbeeke.hyperion.util.Asserts;

public class Mass {
	public static class Builder {
		public Builder(final float m, final Dimension dimension) {

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

}
