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

package ru.windcorp.progressia.client.graphics.backend;

import glm.vec._2.d.Vec2d;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;

public class InputTracker {

	private static final Vec2d CURSOR_POSITION = new Vec2d(Double.NaN, Double.NaN);

	private static final TIntSet PRESSED_KEYS = new TIntHashSet(256);

	private InputTracker() {
	}

	public static double getCursorX() {
		return CURSOR_POSITION.x;
	}

	public static double getCursorY() {
		return CURSOR_POSITION.y;
	}

	public static Vec2d getCursorPosition() {
		return CURSOR_POSITION;
	}

	static void initializeCursorPosition(double x, double y) {
		if (Double.isNaN(CURSOR_POSITION.x)) {
			CURSOR_POSITION.set(x, y);
		}
	}

	public static boolean isKeyPressed(int glfwCode) {
		return PRESSED_KEYS.contains(glfwCode);
	}

	static void setKeyState(int glfwCode, boolean isPressed) {
		if (isPressed) {
			PRESSED_KEYS.add(glfwCode);
		} else {
			PRESSED_KEYS.remove(glfwCode);
		}
	}

	public static TIntSet getPressedKeys() {
		return PRESSED_KEYS;
	}

	static void releaseEverything() {
		PRESSED_KEYS.clear();
	}

}
