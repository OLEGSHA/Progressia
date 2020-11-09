/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.client.graphics;

import glm.vec._3.Vec3;

public class Colors {
	
	public static final int
			WHITE         = 0xFFFFFF,
			BLACK         = 0x000000,
			
			GRAY_4        = 0x444444,
			GRAY          = 0x888888,
			GRAY_A        = 0xAAAAAA,
			
			DEBUG_RED     = 0xFF0000,
			DEBUG_GREEN   = 0x00FF00,
			DEBUG_BLUE    = 0x0000FF,
			
			DEBUG_CYAN    = 0x00FFFF,
			DEBUG_MAGENTA = 0xFF00FF,
			DEBUG_YELLOW  = 0xFFFF00;
	
	public static Vec3 toVector(int rgb) {
		return toVector(rgb, new Vec3());
	}
	
	public static Vec3 toVector(int rgb, Vec3 output) {
		output.x = ((rgb & 0xFF0000) >> 16) / 256f;
		output.y = ((rgb & 0x00FF00) >> 8 ) / 256f;
		output.z = ((rgb & 0x0000FF)      ) / 256f;
		
		return output;
	}

}
