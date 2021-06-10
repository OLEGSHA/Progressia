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
public abstract class BufferedCharReader extends AbstractCharReader {

	protected static final int DEFAULT_BUFFER_SIZE = 256;
	/**
	 * Buffer to store data acquired with {@link #pullChars(char[], int, int)}.
	 * Contains characters for positions <code>[0; bufferNextIndex)</code>.
	 */
	private char[] buffer = new char[DEFAULT_BUFFER_SIZE];

	/**
	 * The index of the next character.
	 */
	private int bufferNextIndex = 0;

	/**
	 * Whether this reader has been buffered completely.
	 */
	private boolean exhausted = false;

	/**
	 * Acquires the next character.
	 * 
	 * @return the character or {@link #DONE} if the end of the reader has been
	 *         reached
	 */
	protected abstract char pullChar();

	/**
	 * Acquires next characters and stores them in the array.
	 * 
	 * @param buffer
	 *            the output array
	 * @param offset
	 *            index of the first character
	 * @param length
	 *            maximum amount of characters to be pulled
	 * @return the amount of characters actually pulled
	 */
	protected int pullChars(char[] buffer, int offset, int length) {
		for (int i = 0; i < length; ++i) {
			if ((buffer[offset + i] = pullChar()) == DONE) {
				return i;
			}
		}

		return length;
	}

	private int pullChars(int offset, int length) {
		if (exhausted || length == 0)
			return 0;

		int pulled = pullChars(buffer, offset, length);
		if (pulled != length) {
			exhausted = true;
		}

		return pulled;
	}

	@Override
	public char current() {
		if (getPosition() < 0) {
			throw new IllegalStateException("Position " + getPosition() + " is invalid");
		}

		if (getPosition() >= bufferNextIndex) {
			if (exhausted)
				return DONE;

			ensureBufferCapacity();

			int needToPull = getPosition() - bufferNextIndex + 1;
			assert needToPull <= buffer.length : "buffer size not ensured!";

			int pulled = pullChars(bufferNextIndex, needToPull);
			bufferNextIndex += pulled;

			if (exhausted)
				return DONE;
		}

		// TODO test the shit out of current()

		return buffer[getPosition()];
	}

	private void ensureBufferCapacity() {
		if (getPosition() < buffer.length)
			return;
		char[] newBuffer = new char[closestGreaterPowerOf2(getPosition())];
		System.arraycopy(buffer, 0, newBuffer, 0, bufferNextIndex);
		buffer = newBuffer;
	}

	/**
	 * @see ru.windcorp.jputil.chars.reader.CharReader#remaining()
	 */
	@Override
	public int remaining() {
		if (exhausted) {
			return Math.max(bufferNextIndex - getPosition(), 0);
		}

		return super.remaining();
	}

}
