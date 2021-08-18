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
import ru.windcorp.progressia.client.graphics.backend.InputTracker;

public abstract class CursorEvent extends InputEvent {

	public CursorEvent(double time) {
		super(time);
	}

	public double getCursorX() {
		return InputTracker.getCursorX();
	}

	public double getCursorY() {
		return InputTracker.getCursorY();
	}

	public Vec2d getCursorPosition() {
		return InputTracker.getCursorPosition();
	}

	@Override
	public abstract CursorEvent snapshot();

}
