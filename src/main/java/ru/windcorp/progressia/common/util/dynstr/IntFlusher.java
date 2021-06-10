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

class IntFlusher {

	public static void flushInt(TCharList sink, int number, int width, boolean alwaysUseSign) {
		int size = stringSize(number);

		boolean isSignNeeded = number != 0 && (number < 0 || alwaysUseSign);
		if (isSignNeeded) {
			size++;
		}

		int needChars = Math.max(size, width);
		reserve(sink, needChars);

		int charPos = flushDigits(number, sink, sink.size());

		if (isSignNeeded) {
			sink.set(--charPos, number > 0 ? '+' : '-');
		}
	}

	/*
	 * Copied from OpenJDK's Integer.stringSize(int)
	 */
	public static int stringSize(int x) {
		int d = 1;
		if (x >= 0) {
			d = 0;
			x = -x;
		}
		int p = -10;
		for (int i = 1; i < 10; i++) {
			if (x > p)
				return i + d;
			p = 10 * p;
		}
		return 10 + d;
	}

	/*
	 * Copied from OpenJDK's Integer.DigitTens and Integer.DigitOnes
	 */
	private static final char[] DIGIT_TENS = { '0', '0', '0', '0', '0', '0', '0', '0', '0', '0', '1', '1', '1', '1',
			'1', '1', '1', '1', '1', '1', '2', '2', '2', '2', '2', '2', '2', '2', '2', '2', '3', '3', '3', '3', '3',
			'3', '3', '3', '3', '3', '4', '4', '4', '4', '4', '4', '4', '4', '4', '4', '5', '5', '5', '5', '5', '5',
			'5', '5', '5', '5', '6', '6', '6', '6', '6', '6', '6', '6', '6', '6', '7', '7', '7', '7', '7', '7', '7',
			'7', '7', '7', '8', '8', '8', '8', '8', '8', '8', '8', '8', '8', '9', '9', '9', '9', '9', '9', '9', '9',
			'9', '9', };

	private static final char[] DIGIT_ONES = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3',
			'4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5',
			'6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6',
			'7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '1', '2', '3', '4', '5', '6', '7',
			'8', '9', };

	/*
	 * Adapted from OpenJDK's Integer.getChars(int, int, byte[])
	 */
	public static int flushDigits(int number, TCharList output, int endIndex) {
		int q, r;
		int charPos = endIndex;

		if (number >= 0) {
			number = -number;
		}

		// Generate two digits per iteration
		while (number <= -100) {
			q = number / 100;
			r = (q * 100) - number;
			number = q;
			output.set(--charPos, DIGIT_ONES[r]);
			output.set(--charPos, DIGIT_TENS[r]);
		}

		// We know there are at most two digits left at this point.
		q = number / 10;
		r = (q * 10) - number;
		output.set(--charPos, (char) ('0' + r));

		// Whatever left is the remaining digit.
		if (q < 0) {
			output.set(--charPos, (char) ('0' - q));
		}

		return charPos;
	}

	private static void reserve(TCharList sink, int needChars) {
		for (int i = 0; i < needChars; ++i) {
			sink.add(' ');
		}
	}

}
