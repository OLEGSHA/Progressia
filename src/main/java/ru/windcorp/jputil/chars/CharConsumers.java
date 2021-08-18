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

import java.util.Objects;

import ru.windcorp.jputil.ArrayUtil;

/**
 * @author Javapony
 */
public class CharConsumers {

	private CharConsumers() {
	}

	public static CharConsumer fillArray(char[] array, int offset, int length) {
		return new ArrayFiller(array, offset, length);
	}

	public static CharConsumer fillArray(char[] array) {
		return fillArray(array, 0, -1);
	}

	private static class ArrayFiller implements CharConsumer {

		final char[] array;
		int i;
		final int end;

		/**
		 * @param array
		 * @param offset
		 * @param length
		 */
		ArrayFiller(char[] array, int offset, int length) {
			this.array = Objects.requireNonNull(array, "array");
			this.end = ArrayUtil.checkArrayStartEnd(array, offset, offset + length);
			this.i = offset;
		}

		/**
		 * @see ru.windcorp.jputil.chars.CharConsumer#accept(char)
		 */
		@Override
		public void accept(char c) {
			if (i == end)
				throw new ArrayIndexOutOfBoundsException(end);
			array[i++] = c;
		}

	}

}
