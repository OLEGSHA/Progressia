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
 
package ru.windcorp.progressia.util;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import ru.windcorp.progressia.common.util.CoordinatePacker;

public class CoordinatePacker3Test {

	@Test
	public void cornerCases() {
		check(0, 0, 0);
		check(0, 0, 42);
		check(0, 42, 0);
		check(42, 0, 0);
		check(1, 1, 1);
		check(-1, -1, -1);
		check(1 << 19, 1 << 19, 1 << 19);
		check((1 << 20) - 1, (1 << 20) - 1, (1 << 20) - 1);
		check(-(1 << 19), -(1 << 19), -(1 << 19));
	}

	@Test
	public void randomValues() {
		Random random = new Random(0);
		int bound = 1 << 20;

		for (int i = 0; i < 1000000; ++i) {
			check(
				random.nextInt(bound) * (random.nextBoolean() ? 1 : -1),
				random.nextInt(bound) * (random.nextBoolean() ? 1 : -1),
				random.nextInt(bound) * (random.nextBoolean() ? 1 : -1)
			);
		}
	}

	private void check(int a, int b, int c) {

		long packed = CoordinatePacker.pack3IntsIntoLong(a, b, c);

		int unpackedA = CoordinatePacker.unpack3IntsFromLong(packed, 0);
		int unpackedB = CoordinatePacker.unpack3IntsFromLong(packed, 1);
		int unpackedC = CoordinatePacker.unpack3IntsFromLong(packed, 2);

		assertEquals(a, unpackedA);
		assertEquals(b, unpackedB);
		assertEquals(c, unpackedC);

	}

}
