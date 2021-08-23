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

package ru.windcorp.progressia.common.util.dynstr;

import gnu.trove.list.TCharList;

class FloatFlusher {

	public static void flushFloat(TCharList sink, float number, int width, int precision, boolean alwaysUseSign) {
		boolean isSignNeeded = !Float.isNaN(number) && !isZero(number, precision) && (number < 0 || alwaysUseSign);
		int size = getSize(number, precision, isSignNeeded);

		int needChars = Math.max(width, size);
		reserve(sink, needChars);

		int charPos = flushDigits(number, precision, sink);

		if (isSignNeeded) {
			sink.set(--charPos, number > 0 ? '+' : '-');
		}
	}

	private static boolean isZero(float number, int precision) {
		int digits = (int) Math.floor(number * pow10(precision));
		return digits == 0;
	}

	private static final char[] NaN_CHARS = "NaN".toCharArray();
	private static final char[] INFINITY_CHARS = "Infinity".toCharArray();

	private static int getSize(float number, int precision, boolean isSignNeeded) {
		if (Float.isNaN(number))
			return NaN_CHARS.length;
		if (number == Float.POSITIVE_INFINITY)
			return (isSignNeeded ? 1 : 0) + INFINITY_CHARS.length;

		int integer = (int) Math.floor(Math.abs(number));
		return (isSignNeeded ? 1 : 0) + IntFlusher.stringSize(integer) + 1 + precision;
	}

	private static void reserve(TCharList sink, int needChars) {
		for (int i = 0; i < needChars; ++i) {
			sink.add(' ');
		}
	}

	private static int flushDigits(float number, int precision, TCharList sink) {
		if (Float.isFinite(number)) {
			return flushFiniteDigits(number, precision, sink);
		} else {
			return flushNonFiniteDigits(number, sink);
		}
	}

	private static int flushFiniteDigits(float number, int precision, TCharList sink) {
		number = Math.abs(number);

		int integer = (int) Math.floor(number);
		int fraction = (int) Math.floor((number - Math.floor(number)) * pow10(precision));

		int charPos = IntFlusher.flushDigits(fraction, sink, sink.size());
		sink.set(--charPos, '.');
		charPos = IntFlusher.flushDigits(integer, sink, charPos);

		return charPos;
	}

	private static float pow10(int precision) {
		float result = 1;
		for (int i = 0; i < precision; ++i)
			result *= 10;
		return result;
	}

	private static int flushNonFiniteDigits(float number, TCharList sink) {
		final char[] chars;

		if (Float.isNaN(number)) {
			chars = NaN_CHARS;
		} else {
			chars = INFINITY_CHARS;
		}

		int offset = sink.size() - chars.length;
		sink.set(offset, chars);
		return offset;
	}

}
