package com.jeroensteenbeeke.worldgen.tectonics;

public class FloatPoint extends Point<Float> {

	public FloatPoint(float x, float y) {
		super(x, y);
	}

	public FloatPoint shift(float dx, float dy,
			final WorldDimension worldDimension) {
		float _x = getX();
		float _y = getY();

		_x += dx;
		_x += _x > 0 ? 0 : worldDimension.getWidth();
		_x -= _x < worldDimension.getWidth() ? 0 : worldDimension.getWidth();

		_y += dy;
		_y += _y > 0 ? 0 : worldDimension.getHeight();
		_y -= _y < worldDimension.getHeight() ? 0 : worldDimension.getHeight();

		return new FloatPoint(_x, _y);
	}

	public IntPoint toInt() {
		return new IntPoint(getX().intValue(), getY().intValue());
	}
}
