/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.windcorp.progressia.client.graphics.input;

import glm.vec._2.d.Vec2d;

public class WheelScrollEvent extends WheelEvent {

	private final Vec2d offset = new Vec2d();

	protected WheelScrollEvent(double xOffset, double yOffset, double time) {
		super(time);
		this.offset.set(xOffset, yOffset);
	}

	protected WheelScrollEvent(Vec2d offset, double time) {
		this(offset.x, offset.y, time);
	}

	public boolean isUp() {
		return getY() > 0;
	}

	public boolean isDown() {
		return getY() < 0;
	}

	public boolean isRight() {
		return getX() > 0;
	}

	public boolean isLeft() {
		return getX() < 0;
	}

	public boolean hasVerticalMovement() {
		return getY() != 0;
	}

	public boolean hasHorizontalMovement() {
		return getX() != 0;
	}

	public double getX() {
		return getOffset().x;
	}

	public double getY() {
		return getOffset().y;
	}

	public Vec2d getOffset() {
		return offset;
	}

	@Override
	public WheelEvent snapshot() {
		return new WheelScrollEvent(getOffset(), getTime());
	}
}
