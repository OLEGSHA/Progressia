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

/**
 * @author Javapony
 */
public class StringCharReader extends AbstractCharReader {

	private final String str;
	private final int offset;
	private final int length;

	public StringCharReader(String str, int offset, int length) {
		this.str = Objects.requireNonNull(str, "str");

		if (length < 0)
			length = str.length();

		int end = offset + length;
		if (end > str.length() || offset < 0)
			throw new IllegalArgumentException(
					"String contains [0; " + str.length() + "), requested [" + offset + "; " + end + ")");

		this.offset = offset;
		this.length = length;
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
		return str.charAt(position + offset);
	}

	/**
	 * @see ru.windcorp.jputil.chars.reader.CharReader#remaining()
	 */
	@Override
	public int remaining() {
		return length - position;
	}

}
