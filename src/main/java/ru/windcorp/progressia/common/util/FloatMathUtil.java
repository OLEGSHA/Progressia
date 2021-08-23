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

package ru.windcorp.progressia.common.util;

public class FloatMathUtil {

	public static final float PI_F = (float) Math.PI;

	public static float floor(float x) {
		return (float) Math.floor(x);
	}

	public static float normalizeAngle(float a) {
		return a - 2 * PI_F * floor((a + PI_F) / (2 * PI_F));
	}

	public static float sin(float x) {
		return (float) Math.sin(x);
	}

	public static float cos(float x) {
		return (float) Math.cos(x);
	}

	public static float tan(float x) {
		return (float) Math.tan(x);
	}

	private FloatMathUtil() {
	}

}
