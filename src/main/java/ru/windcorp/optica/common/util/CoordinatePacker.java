/*******************************************************************************
 * Optica
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
package ru.windcorp.optica.common.util;

public class CoordinatePacker {
	
	private static final int BITS_3_INTS_INTO_LONG;
	private static final long MASK_3_INTS_INTO_LONG;
	
	static {
		BITS_3_INTS_INTO_LONG = 64 / 3;
		
		/*
		 * What happens below:
		 * 
		 * 1. 1 << BITS_3_INTS_INTO_LONG:
		 *    0000 ... 00100 ... 0000
		 *                \_________/ - BITS_3_INTS_INTO_LONG zeros
		 *                
		 * 2. (1 << BITS_3_INTS_INTO_LONG) - 1:
		 *    0000 ... 00011 ... 1111
		 *                \_________/ - BITS_3_INTS_INTO_LONG ones - WIN
		 */
		
		MASK_3_INTS_INTO_LONG = (1 << BITS_3_INTS_INTO_LONG) - 1;
	}
	
	public static long pack3IntsIntoLong(int a, int b, int c) {
		return
				((a & MASK_3_INTS_INTO_LONG) << (2 * BITS_3_INTS_INTO_LONG)) |
				((b & MASK_3_INTS_INTO_LONG) << (1 * BITS_3_INTS_INTO_LONG)) |
				((c & MASK_3_INTS_INTO_LONG) << (0 * BITS_3_INTS_INTO_LONG));
	}
	
	public static int unpack3IntsFromLong(long packed, int index) {
		if (index < 0 || index >= 3) {
			throw new IllegalArgumentException("Invalid index " + index);
		}
		
		int result = (int) (
				(packed >>> ((2 - index) * BITS_3_INTS_INTO_LONG))
				& MASK_3_INTS_INTO_LONG
		);
		
		final long signMask = ((MASK_3_INTS_INTO_LONG + 1) >> 1);
		
		if ((result & signMask) != 0) {
			result |= ~MASK_3_INTS_INTO_LONG;
		}
		
		return result;
	}

}
