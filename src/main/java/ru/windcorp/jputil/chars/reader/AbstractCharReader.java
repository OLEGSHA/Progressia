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

/**
 * @author Javapony
 */
public abstract class AbstractCharReader implements CharReader {

	protected static final int DEFAULT_MARK_STACK_SIZE = 8;

	/**
	 * Current position of this CharReader. The reader maps its input to
	 * positions starting from 0. Positions that are negative or lower than 0
	 * are invalid. {@link #current()} will throw an exception if position is
	 * invalid.
	 */
	protected int position = 0;

	private int[] marks = new int[DEFAULT_MARK_STACK_SIZE];
	private int nextMark = 0;

	protected static int closestGreaterPowerOf2(int x) {
		x |= x >> 1;
		x |= x >> 2;
		x |= x >> 4;
		x |= x >> 8;
		x |= x >> 16;
		return x + 1;
	}

	/**
	 * @see ru.windcorp.jputil.chars.reader.CharReader#getPosition()
	 */
	@Override
	public int getPosition() {
		return position;
	}

	/**
	 * @see ru.windcorp.jputil.chars.reader.CharReader#setPosition(int)
	 */
	@Override
	public int setPosition(int position) {
		this.position = position;
		return position;
	}

	/**
	 * @see ru.windcorp.jputil.chars.reader.CharReader#mark()
	 */
	@Override
	public int mark() {
		ensureMarksCapacity();
		marks[nextMark++] = position;
		return position;
	}

	/**
	 * @see ru.windcorp.jputil.chars.reader.CharReader#forget()
	 */
	@Override
	public int forget() {
		return marks[--nextMark];
	}

	private void ensureMarksCapacity() {
		if (nextMark < marks.length)
			return;
		int[] newMarks = new int[closestGreaterPowerOf2(nextMark)];
		System.arraycopy(marks, 0, newMarks, 0, nextMark);
		marks = newMarks;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("\"");

		mark();
		position = 0;
		sb.append(getChars());
		reset();

		sb.append("\"\n ");
		for (int i = 0; i < position; ++i)
			sb.append(' ');
		sb.append("^ (pos " + position + ")");
		return sb.toString();
	}

}
