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

package ru.windcorp.jputil.chars.reader;

import java.util.Objects;

import ru.windcorp.jputil.ArrayUtil;

/**
 * @author Javapony
 */
public class ArrayCharReader extends AbstractCharReader {

	private final char[] array;
	private final int offset;
	private final int length;

	public ArrayCharReader(char[] array, int offset, int length) {
		this.array = Objects.requireNonNull(array, "array");
		this.length = ArrayUtil.checkArrayOffsetLength(array, offset, length);
		this.offset = offset;
	}

	/**
	 * @see ru.windcorp.jputil.chars.reader.CharReader#current()
	 */
	@Override
	public char current() {
		if (position >= length)
			return DONE;
		if (position < 0)
			throw new IllegalStateException("Position " + position + " is invalid");
		return array[position + offset];
	}

	/**
	 * @see ru.windcorp.jputil.chars.reader.CharReader#remaining()
	 */
	@Override
	public int remaining() {
		return length - position;
	}

}
