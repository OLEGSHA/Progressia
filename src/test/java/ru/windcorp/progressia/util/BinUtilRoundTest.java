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

import ru.windcorp.progressia.common.util.BinUtil;

public class BinUtilRoundTest {

	@Test
	public void cornerCases() {
		test(1);
		test(2);
		test(3);
		test(4);
		test(7);
		test(8);
		test(9);

		test(1023);
		test(1024);
		test(1025);

		test((1 << 16) - 1);
		test(1 << 16);
		test((1 << 16) + 1);
	}

	@Test
	public void random() {
		Random random = new Random(0);

		for (int i = 0; i < 10000; ++i) {
			test(random.nextInt((1 << 30) - 2) + 1);
		}
	}

	void test(int x) {
		assertEquals("Round, x = " + x, referenceRound(x), BinUtil.roundToGreaterPowerOf2(x));
		assertEquals("Greater, x = " + x, referenceGreater(x), BinUtil.closestGreaterPowerOf2(x));
	}

	int referenceGreater(int x) {
		int p;
		for (p = 1; p <= x; p *= 2)
			;
		return p;
	}

	int referenceRound(int x) {
		int p;
		for (p = 1; p < x; p *= 2)
			;
		return p;
	}

}
