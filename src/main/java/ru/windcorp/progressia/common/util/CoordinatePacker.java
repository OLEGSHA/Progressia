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

import glm.vec._2.i.Vec2i;
import glm.vec._3.i.Vec3i;

public class CoordinatePacker {

	private static final int BITS_3_INTS_INTO_LONG;
	private static final long MASK_3_INTS_INTO_LONG;

	private static final int BITS_2_INTS_INTO_LONG;
	private static final long MASK_2_INTS_INTO_LONG;

	static {
		BITS_3_INTS_INTO_LONG = 64 / 3;

		/*
		 * What happens below: 1. 1 << BITS_3_INTS_INTO_LONG: 0000 ... 00100 ...
		 * 0000 \_________/ - BITS_3_INTS_INTO_LONG zeros 2. (1 <<
		 * BITS_3_INTS_INTO_LONG) - 1: 0000 ... 00011 ... 1111 \_________/ -
		 * BITS_3_INTS_INTO_LONG ones - WIN
		 */

		MASK_3_INTS_INTO_LONG = (1l << BITS_3_INTS_INTO_LONG) - 1;

		BITS_2_INTS_INTO_LONG = 64 / 2;
		MASK_2_INTS_INTO_LONG = (1l << BITS_2_INTS_INTO_LONG) - 1;
	}

	public static long pack3IntsIntoLong(int a, int b, int c) {
		return ((a & MASK_3_INTS_INTO_LONG) << (2 * BITS_3_INTS_INTO_LONG))
				| ((b & MASK_3_INTS_INTO_LONG) << (1 * BITS_3_INTS_INTO_LONG))
				| ((c & MASK_3_INTS_INTO_LONG) << (0 * BITS_3_INTS_INTO_LONG));
	}

	public static long pack3IntsIntoLong(Vec3i v) {
		return pack3IntsIntoLong(v.x, v.y, v.z);
	}

	public static int unpack3IntsFromLong(long packed, int index) {
		if (index < 0 || index >= 3) {
			throw new IllegalArgumentException("Invalid index " + index);
		}

		int result = (int) ((packed >>> ((2 - index) * BITS_3_INTS_INTO_LONG)) & MASK_3_INTS_INTO_LONG);

		final long signMask = ((MASK_3_INTS_INTO_LONG + 1) >> 1);

		if ((result & signMask) != 0) {
			result |= ~MASK_3_INTS_INTO_LONG;
		}

		return result;
	}

	public static Vec3i unpack3IntsFromLong(long packed, Vec3i output) {
		if (output == null)
			output = new Vec3i();

		output.set(unpack3IntsFromLong(packed, 0), unpack3IntsFromLong(packed, 1), unpack3IntsFromLong(packed, 2));

		return output;
	}

	public static long pack2IntsIntoLong(int a, int b) {
		return ((a & MASK_2_INTS_INTO_LONG) << (1 * BITS_2_INTS_INTO_LONG))
				| ((b & MASK_2_INTS_INTO_LONG) << (0 * BITS_2_INTS_INTO_LONG));
	}

	public static long pack2IntsIntoLong(Vec2i v) {
		return pack2IntsIntoLong(v.x, v.y);
	}

	public static int unpack2IntsFromLong(long packed, int index) {
		if (index < 0 || index >= 2) {
			throw new IllegalArgumentException("Invalid index " + index);
		}

		int result = (int) ((packed >>> ((1 - index) * BITS_2_INTS_INTO_LONG)) & MASK_2_INTS_INTO_LONG);

		return result;
	}

	public static Vec2i unpack2IntsFromLong(long packed, Vec2i output) {
		if (output == null)
			output = new Vec2i();

		output.set(unpack2IntsFromLong(packed, 0), unpack2IntsFromLong(packed, 1));

		return output;
	}

}
