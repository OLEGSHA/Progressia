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
 
package ru.windcorp.progressia.client.graphics;

import glm.vec._4.Vec4;

public class Colors {

	public static final Vec4 WHITE = toVector(0xFFFFFFFF),
		BLACK = toVector(0xFF000000),

		GRAY_4 = toVector(0xFF444444),
		GRAY = toVector(0xFF888888),
		GRAY_A = toVector(0xFFAAAAAA),

		DEBUG_RED = toVector(0xFFFF0000),
		DEBUG_GREEN = toVector(0xFF00FF00),
		DEBUG_BLUE = toVector(0xFF0000FF),
		DEBUG_CYAN = toVector(0xFF00FFFF),
		DEBUG_MAGENTA = toVector(0xFFFF00FF),
		DEBUG_YELLOW = toVector(0xFFFFFF00),
	
		LIGHT_GRAY = toVector(0xFFCBCBD0),
		BLUE = toVector(0xFF37A2E6),
		HOVER_BLUE = toVector(0xFFC3E4F7),
		DISABLED_GRAY = toVector(0xFFE5E5E5),
		DISABLED_BLUE = toVector(0xFFB2D8ED);

	public static Vec4 toVector(int argb) {
		return toVector(argb, new Vec4());
	}

	public static Vec4 multiplyRGB(Vec4 color, float multiplier) {
		return color.mul(multiplier, multiplier, multiplier, 1);
	}

	public static Vec4 multiplyRGB(Vec4 color, float multiplier, Vec4 output) {
		if (output == null)
			output = new Vec4();
		return color.mul(multiplier, multiplier, multiplier, 1, output);
	}

	public static Vec4 toVector(int argb, Vec4 output) {
		output.w = ((argb & 0xFF000000) >>> 24) / (float) 0xFF; // Alpha
		output.x = ((argb & 0x00FF0000) >>> 16) / (float) 0xFF; // Red
		output.y = ((argb & 0x0000FF00) >>> 8) / (float) 0xFF; // Green
		output.z = ((argb & 0x000000FF)) / (float) 0xFF; // Blue

		return output;
	}

}
