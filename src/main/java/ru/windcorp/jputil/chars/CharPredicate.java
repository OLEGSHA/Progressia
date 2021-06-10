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

package ru.windcorp.jputil.chars;

import java.util.Arrays;
import java.util.function.IntPredicate;

import ru.windcorp.jputil.ArrayUtil;

@FunctionalInterface
public interface CharPredicate {

	boolean test(char c);

	public static CharPredicate and(CharPredicate first, CharPredicate second) {
		return c -> first.test(c) && second.test(c);
	}

	public static CharPredicate or(CharPredicate first, CharPredicate second) {
		return c -> first.test(c) || second.test(c);
	}

	public static CharPredicate negate(CharPredicate predicate) {
		return c -> !predicate.test(c);
	}

	public static IntPredicate toInt(CharPredicate predicate) {
		return i -> predicate.test((char) i);
	}

	public static CharPredicate toChar(IntPredicate predicate) {
		return predicate::test;
	}

	public static CharPredicate forArray(char... chars) {
		if (chars.length == 0) {
			return c -> false;
		}

		if (chars.length == 1) {
			return forChar(chars[0]);
		}

		if (chars.length < 16) {
			return c -> ArrayUtil.firstIndexOf(chars, c) >= 0;
		} else {
			final char[] sorted = Arrays.copyOf(chars, chars.length);
			Arrays.sort(sorted);
			return c -> Arrays.binarySearch(chars, c) >= 0;
		}
	}

	public static CharPredicate forChar(final char c) {
		return given -> given == c;
	}

	public static CharPredicate forRange(final char minInclusive, final char maxExclusive) {
		if (minInclusive > maxExclusive) {
			throw new IllegalArgumentException("min > max: " + minInclusive + " > " + maxExclusive);
		}

		if (minInclusive == maxExclusive) {
			return c -> false;
		}

		return c -> c >= minInclusive && c < maxExclusive;
	}

}
