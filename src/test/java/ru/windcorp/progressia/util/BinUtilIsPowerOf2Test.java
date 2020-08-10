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
package ru.windcorp.progressia.util;

import static org.junit.Assert.assertEquals;

import java.util.Random;

import org.junit.Test;

import ru.windcorp.progressia.common.util.BinUtil;

public class BinUtilIsPowerOf2Test {
	
	@Test
	public void cornerCases() {
		test(-1);
		test(0);
		test(1);
		
		test(15);
		test(16);
		test(17);
		
		test(1 << 30);
		test(Integer.MAX_VALUE);
		test(Integer.MIN_VALUE);
	}
	
	@Test
	public void random() {
		Random random = new Random(0);
		
		for (int x = 0; x < 10000; ++x) {
			test(x);
		}
		
		for (int i = 0; i < 10000; ++i) {
			test(random.nextInt());
		}
	}
	
	void test(int x) {
		assertEquals("Round, x = " + x, referenceIsPowerOf2(x), BinUtil.isPowerOf2(x));
	}
	
	boolean referenceIsPowerOf2(int x) {
		for (int power = 1; power > 0; power *= 2) {
			if (x == power) {
				return true;
			}
		}
		
		return false;
	}

}
