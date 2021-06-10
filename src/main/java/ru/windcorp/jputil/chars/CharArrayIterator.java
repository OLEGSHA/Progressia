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

import java.text.CharacterIterator;

public class CharArrayIterator implements CharacterIterator {

	private final char[] array;
	private int pos;

	public CharArrayIterator(char[] array) {
		this.array = array;
	}

	public CharArrayIterator(String src) {
		this(src.toCharArray());
	}

	@Override
	public char first() {
		pos = 0;
		if (array.length != 0) {
			return array[pos];
		}
		return DONE;
	}

	@Override
	public char last() {
		pos = array.length;
		if (array.length != 0) {
			pos -= 1;
			return array[pos];
		}
		return DONE;
	}

	@Override
	public char current() {
		if (array.length != 0 && pos < array.length) {
			return array[pos];
		}
		return DONE;
	}

	@Override
	public char next() {
		pos += 1;
		if (pos >= array.length) {
			pos = array.length;
			return DONE;
		}
		return current();
	}

	@Override
	public char previous() {
		if (pos == 0) {
			return DONE;
		}
		pos -= 1;
		return current();
	}

	@Override
	public char setIndex(int position) {
		if (position < 0 || position > array.length) {
			throw new IllegalArgumentException("bad position: " + position);
		}

		pos = position;

		if (pos != array.length && array.length != 0) {
			return array[pos];
		}
		return DONE;
	}

	@Override
	public int getBeginIndex() {
		return 0;
	}

	@Override
	public int getEndIndex() {
		return array.length;
	}

	@Override
	public int getIndex() {
		return pos;
	}

	// @SuppressWarnings("all") Just STFU, this _is_ terrific

	// SonarLint: "clone" should not be overridden (java:S2975)
	// And I wouldn't have done that if only CharacterIterator had not required
	// exception safety.
	// SonarLint: "toString()" and "clone()" methods should not return null
	// (java:S2225)
	// The clause is unreachable: CharacterArrayIterator implements Cloneable
	// and superclass is Object.
	@SuppressWarnings({ "squid:S2975", "squid:S2225" })

	@Override
	public CharArrayIterator clone() {
		try {
			return (CharArrayIterator) super.clone();
		} catch (CloneNotSupportedException cnse) {
			// Impossible
			return null;
		}
	}

}
