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

import java.io.IOException;
import java.util.Arrays;

import ru.windcorp.jputil.chars.CharPredicate;
import ru.windcorp.jputil.chars.EscapeException;
import ru.windcorp.jputil.chars.Escaper;

/**
 * @author Javapony
 */

// SonarLint: Constants should not be defined in interfaces (java:S1214)
// DONE is an essential part of the interface
@SuppressWarnings("squid:S1214")

public interface CharReader {

	char DONE = '\uFFFF';

	char current();

	int getPosition();

	int setPosition(int position);

	default char next() {
		return advance(1);
	}

	default char previous() {
		return rollBack(1);
	}

	default char consume() {
		char c = current();
		advance(1);
		return c;
	}

	default char advance(int forward) {
		setPosition(getPosition() + forward);
		return current();
	}

	default char rollBack(int backward) {
		return advance(-backward);
	}

	default boolean isEnd() {
		return current() == DONE;
	}

	default boolean has() {
		return current() != DONE;
	}

	default boolean is(char c) {
		return current() == c;
	}

	default int getChars(char[] output, int offset, int length) {
		for (int i = 0; i < length; ++i) {
			if ((output[offset + i] = current()) == DONE) {
				return i;
			}
			next();
		}

		return length;
	}

	default int getChars(char[] output) {
		return getChars(output, 0, output.length);
	}

	default char[] getChars(int length) {
		char[] result = new char[length];
		int from = getChars(result);
		if (from != length)
			Arrays.fill(result, from, length, DONE);
		return result;
	}

	default char[] getChars() {
		return getChars(remaining());
	}

	default String getString(int length) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length && !isEnd(); ++i)
			sb.append(consume());
		return sb.toString();
	}

	default String getString() {
		return getString(Integer.MAX_VALUE);
	}

	default boolean match(CharSequence seq) {
		for (int i = 0; i < seq.length(); ++i) {
			if (isEnd())
				return false;
			if (current() != seq.charAt(i))
				return false;
			next();
		}

		return true;
	}

	default boolean matchOrReset(CharSequence seq) {
		mark();
		if (match(seq)) {
			forget();
			return true;
		} else {
			reset();
			return false;
		}
	}

	default boolean match(char[] array) {
		for (int i = 0; i < array.length; ++i) {
			if (isEnd())
				return false;
			if (current() != array[i])
				return false;
			next();
		}

		return true;
	}

	default boolean matchOrReset(char[] array) {
		mark();
		if (match(array)) {
			forget();
			return true;
		} else {
			reset();
			return false;
		}
	}

	default int skip(CharPredicate condition) {
		int i = 0;

		while (has() && condition.test(current())) {
			i++;
			next();
		}

		return i;
	}

	default int skipWhitespace() {
		return skip(Character::isWhitespace);
	}

	/**
	 * Skips to the end of the current line. Both <code>"\n"</code>,
	 * <code>"\r"</code> and <code>"\r\n"</code> are considered line separators.
	 * 
	 * @return the amount of characters in the skipped line
	 */
	default int skipLine() {
		int i = 0;

		while (!isEnd()) {
			if (current() == '\r') {
				if (next() == '\n') {
					next();
				}
				break;
			} else if (current() == '\n') {
				next();
				break;
			}

			i++;
			next();
		}

		return i;
	}

	default char[] readWhile(CharPredicate condition) {
		return readUntil(CharPredicate.negate(condition));
	}

	default char[] readUntil(CharPredicate condition) {
		mark();
		int length = 0;
		while (!isEnd() && !condition.test(current())) {
			length++;
			next();
		}
		reset();

		char[] result = new char[length];
		for (int i = 0; i < length; ++i)
			result[i] = consume();
		return result;
	}

	default char[] readWord() {
		skipWhitespace();
		return readUntil(Character::isWhitespace);
	}

	default char[] readWord(Escaper escaper, char quotes) throws EscapeException {
		skipWhitespace();

		if (current() == quotes) {
			return escaper.unescape(this, quotes);
		} else {
			return readWord();
		}
	}

	default char[] readLine() {
		mark();
		int length = skipLine();
		reset();

		char[] result = new char[length];
		for (int i = 0; i < result.length; ++i)
			result[i] = consume();
		return result;
	}

	default int remaining() {
		mark();
		int result = 0;

		while (consume() != DONE)
			result++;

		reset();
		return result;
	}

	int mark();

	int forget();

	default int reset() {
		return setPosition(forget());
	}

	default IOException getLastException() {
		return null;
	}

	default void resetLastException() {
		// Do nothing
	}

	default boolean hasErrored() {
		return getLastException() != null;
	}

}
