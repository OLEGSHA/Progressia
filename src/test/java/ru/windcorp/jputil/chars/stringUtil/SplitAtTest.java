/*
 * JPUtil
 * Copyright (C)  2019-2021  OLEGSHA/Javapony and contributors
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
 
package ru.windcorp.jputil.chars.stringUtil;

import static org.junit.Assert.assertArrayEquals;

import java.util.Random;

import org.junit.Test;

import ru.windcorp.jputil.chars.StringUtil;

public class SplitAtTest {

	@Test
	public void testExamplesFromDocs() {
		test("a.b.c", new int[] { 1, 3 }, new String[] { "a", "b", "c" });
		test("a..b", new int[] { 1, 2 }, new String[] { "a", "", "b" });
		test(".b.", new int[] { 0, 2 }, new String[] { "", "b", "" });
		test("a.b", new int[] { 1, 1, 1 }, new String[] { "a", "", "", "b" });
	}

	@Test
	public void testIndexPermutations() {
		Random random = new Random(0);

		int stringLength = 1000;
		char[] chars = new char[stringLength];

		for (int i = 0; i < stringLength; ++i) {
			chars[i] = (char) ('a' + random.nextInt('z' - 'a'));
		}

		String src = new String(chars);

		int[] indices = new int[100];

		for (int i = 0; i < indices.length; ++i) {
			indices[i] = random.nextInt(stringLength);
		}

		String[] expected = StringUtil.splitAt(src, indices);

		for (int i = 0; i < 10000; ++i) {
			shuffleArray(indices, random);

			int[] copy = indices.clone();
			test(src, indices, expected);
			assertArrayEquals(indices, copy); // Make sure indices array hasn't
												// changed
		}
	}

	// Shamelessly copied from
	// https://stackoverflow.com/a/1520212/4463352
	// Thanks, https://stackoverflow.com/users/15459/philho!

	// Implementing Fisher–Yates shuffle
	private static void shuffleArray(int[] ar, Random random) {
		for (int i = ar.length - 1; i > 0; i--) {
			int index = random.nextInt(i + 1);
			// Simple swap
			int a = ar[index];
			ar[index] = ar[i];
			ar[i] = a;
		}
	}

	private void test(String string, int[] at, String[] expecteds) {
		assertArrayEquals(expecteds, StringUtil.splitAt(string, at));
	}

}
