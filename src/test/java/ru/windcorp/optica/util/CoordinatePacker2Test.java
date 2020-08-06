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
package ru.windcorp.optica.util;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import ru.windcorp.optica.common.util.CoordinatePacker;

public class CoordinatePacker2Test {
	
	@Test
	public void cornerCases() {
		check(0, 0);
		check(0, 42);
		check(42, 0);
		check(1, 1);
		check(-1, -1);
		
		for (int a : new int[] {Integer.MAX_VALUE, Integer.MIN_VALUE, 0}) {
			for (int b : new int[] {Integer.MAX_VALUE, Integer.MIN_VALUE, 0}) {
				check(a, b);
			}
		}
	}
	
	@Test
	public void randomValues() {
		Random random = new Random(0);;
		
		for (int i = 0; i < 1000000; ++i) {
			check(
					random.nextInt(),
					random.nextInt()
			);
		}
	}

	private void check(int a, int b) {
		
		long packed = CoordinatePacker.pack2IntsIntoLong(a, b);
		
		int unpackedA = CoordinatePacker.unpack2IntsFromLong(packed, 0);
		int unpackedB = CoordinatePacker.unpack2IntsFromLong(packed, 1);
		
		assertEquals(a, unpackedA);
		assertEquals(b, unpackedB);
		
	}

}
