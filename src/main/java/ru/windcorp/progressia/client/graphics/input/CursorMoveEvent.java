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

import glm.vec._2.Vec2;
import glm.vec._2.d.Vec2d;

public class CursorMoveEvent extends CursorEvent {

	private final Vec2d newPosition = new Vec2d();

	protected CursorMoveEvent(double newX, double newY, double time) {
		super(time);
		newPosition.set(newX, newY);
	}

	protected CursorMoveEvent(Vec2d newPos, double time) {
		this(newPos.x, newPos.y, time);
	}

	@Override
	public double getCursorX() {
		return getCursorPosition().x;
	}

	@Override
	public double getCursorY() {
		return getCursorPosition().y;
	}

	@Override
	public Vec2d getCursorPosition() {
		return getNewPosition();
	}

	public double getNewX() {
		return getNewPosition().x;
	}

	public double getNewY() {
		return getNewPosition().y;
	}

	public Vec2d getNewPosition() {
		return newPosition;
	}

	public double getPreviousX() {
		return getPreviousPosition().x;
	}

	public double getPreviousY() {
		return getPreviousPosition().y;
	}

	public Vec2d getPreviousPosition() {
		return super.getCursorPosition();
	}

	public double getChangeX() {
		return getNewX() - getPreviousX();
	}

	public double getChangeY() {
		return getNewY() - getPreviousY();
	}

	public Vec2 getChange(Vec2 result) {
		return result.set(getChangeX(), getChangeY());
	}

	@Override
	public CursorMoveEvent snapshot() {
		return new StaticMouseMoveEvent(getPreviousPosition(), getNewPosition(), getTime());
	}

	private class StaticMouseMoveEvent extends CursorMoveEvent {

		private final Vec2d previousPosition = new Vec2d();

		public StaticMouseMoveEvent(Vec2d previousPosition, Vec2d newPosition, double time) {
			super(newPosition, time);
			this.previousPosition.set(previousPosition.x, previousPosition.y);
		}

		@Override
		public Vec2d getPreviousPosition() {
			return previousPosition;
		}

	}

}
