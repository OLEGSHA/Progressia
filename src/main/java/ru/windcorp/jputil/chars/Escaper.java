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

import ru.windcorp.jputil.ArrayUtil;
import ru.windcorp.jputil.chars.reader.CharReader;
import ru.windcorp.jputil.chars.reader.CharReaders;

public class Escaper {

	public static class EscaperBuilder {
		private char escapeChar = '\\';
		private char unicodeEscapeChar = 'u';
		private char[] safes = null;
		private char[] unsafes = null;

		private boolean preferUnicode = false;
		private boolean strict = true;

		public EscaperBuilder withEscapeChar(char escapeChar) {
			this.escapeChar = escapeChar;
			return this;
		}

		public EscaperBuilder withUnicodeEscapeChar(char unicodeEscapeChar) {
			this.unicodeEscapeChar = unicodeEscapeChar;
			return this;
		}

		public EscaperBuilder withChars(char[] safes, char[] unsafes) {
			this.safes = safes;
			this.unsafes = unsafes;
			return this;
		}

		public EscaperBuilder withChars(String safes, String unsafes) {
			this.safes = safes.toCharArray();
			this.unsafes = unsafes.toCharArray();
			return this;
		}

		public EscaperBuilder withChars(char[] chars) {
			this.safes = this.unsafes = chars;
			return this;
		}

		public EscaperBuilder withChars(String chars) {
			this.safes = this.unsafes = chars.toCharArray();
			return this;
		}

		public EscaperBuilder withSafes(char[] safes) {
			this.safes = safes;
			return this;
		}

		public EscaperBuilder withSafes(String safes) {
			this.safes = safes.toCharArray();
			return this;
		}

		public EscaperBuilder withUnsafes(char[] unsafes) {
			this.unsafes = unsafes;
			return this;
		}

		public EscaperBuilder withUnsafes(String unsafes) {
			this.unsafes = unsafes.toCharArray();
			return this;
		}

		public EscaperBuilder preferUnicode(boolean preferUnicode) {
			this.preferUnicode = preferUnicode;
			return this;
		}

		public EscaperBuilder strict(boolean strict) {
			this.strict = strict;
			return this;
		}

		public Escaper build() {
			return new Escaper(escapeChar, unicodeEscapeChar, safes, unsafes, preferUnicode, strict);
		}

	}

	public static final Escaper JAVA = new Escaper('\\', 'u', "tbnrf'\"".toCharArray(), "\t\b\n\r\f\'\"".toCharArray(),
			true, true);

	private final char escapeChar;
	private final char unicodeEscapeChar;
	private final char[] safes;
	private final char[] unsafes;

	private final boolean preferUnicode;
	private final boolean strict;

	protected Escaper(char escapeChar, char unicodeEscapeChar, char[] safes, char[] unsafes, boolean preferUnicode,
			boolean strict) {
		this.escapeChar = escapeChar;
		this.unicodeEscapeChar = unicodeEscapeChar;
		this.safes = safes;
		this.unsafes = unsafes;
		this.preferUnicode = preferUnicode;
		this.strict = strict;

		int duplicate;
		if ((duplicate = ArrayUtil.hasDuplicates(safes)) != -1)
			throw new IllegalArgumentException("Duplicate safe character '" + safes[duplicate] + "'");

		if ((duplicate = ArrayUtil.hasDuplicates(unsafes)) != -1)
			throw new IllegalArgumentException("Duplicate unsafe character '" + unsafes[duplicate] + "'");

		for (char c : safes) {
			if (c == escapeChar)
				throw new IllegalArgumentException("Safe characters contain escape chatacter");
			if (c == unicodeEscapeChar)
				throw new IllegalArgumentException("Safe characters contain Unicode escape chatacter");
		}

		for (char c : unsafes) {
			if (c == escapeChar)
				throw new IllegalArgumentException(
						"Unsafe characters contain escape chatacter (escape character is escaped automatically)");
			if (c == unicodeEscapeChar)
				throw new IllegalArgumentException("Unsafe characters contain Unicode escape chatacter");
		}
	}

	public static EscaperBuilder create() {
		return new EscaperBuilder();
	}

	/*
	 * Logic - escape
	 */

	public void escape(CharReader src, int length, CharPredicate until, CharConsumer output) {
		int end;
		if (length < 0)
			end = Integer.MAX_VALUE;
		else
			end = src.getPosition() + length;
		while (src.has() && src.getPosition() < end && (until == null || !until.test(src.current())))
			escape(src.consume(), output);
	}

	public void escape(char c, CharConsumer output) {
		if (c == escapeChar) {
			output.accept(escapeChar);
			output.accept(escapeChar);
			return;
		}

		int index = ArrayUtil.firstIndexOf(unsafes, c);

		if (index >= 0) {
			output.accept(escapeChar);
			output.accept(safes[index]);
		} else {
			if (preferUnicode && !isRegular(c)) {
				escapeAsHex(c, output);
			} else {
				output.accept(c);
			}
		}
	}

	// SonarLint: Assignments should not be made from within sub-expressions
	// (java:S1121)
	// Seems self-evident enough
	@SuppressWarnings("squid:S1121")

	private void escapeAsHex(char c, CharConsumer output) {
		output.accept(escapeChar);
		output.accept(unicodeEscapeChar);
		output.accept(StringUtil.hexDigit(c >>= (4 * 3)));
		output.accept(StringUtil.hexDigit(c >>= (4 * 2)));
		output.accept(StringUtil.hexDigit(c >>= (4 * 1)));
		output.accept(StringUtil.hexDigit(c >> (4 * 0)));
	}

	public int getEscapedLength(CharReader src, int length, CharPredicate until) {
		int end;
		if (length < 0)
			end = Integer.MAX_VALUE;
		else
			end = src.getPosition() + length;

		int result = 0;

		while (src.has() && src.getPosition() < end && (until == null || !until.test(src.current()))) {
			result += getEscapedLength(src.consume());
		}

		return result;
	}

	public int getEscapedLength(char c) {
		if (c == escapeChar || ArrayUtil.firstIndexOf(unsafes, c) >= 0)
			return 2;
		else {
			if (preferUnicode && !isRegular(c))
				return 6;
			else
				return 1;
		}
	}

	/*
	 * Logic - unescape
	 */

	public void unescape(CharReader src, int length, CharPredicate until, CharConsumer output) throws EscapeException {
		int end;
		if (length < 0)
			end = Integer.MAX_VALUE;
		else
			end = src.getPosition() + length;
		while (src.has() && src.getPosition() < end && (until == null || !until.test(src.current()))) {
			output.accept(unescapeOneSequence(src));
		}
	}

	public char unescapeOneSequence(CharReader src) throws EscapeException {
		int resetPos = src.getPosition();
		try {
			if (src.current() == escapeChar) {
				src.next();

				if (src.isEnd())
					throw new EscapeException("Incomplete escape sequence at the end");

				if (src.current() == escapeChar) {
					src.next();
					return escapeChar;
				}

				if (src.current() == unicodeEscapeChar) {
					src.next();
					return (char) (hexValue(src.consume()) << (4 * 3) | hexValue(src.consume()) << (4 * 2)
							| hexValue(src.consume()) << (4 * 1) | hexValue(src.consume()) << (4 * 0));
				}

				int index = ArrayUtil.firstIndexOf(safes, src.current());
				if (index >= 0) {
					src.next();
					return unsafes[index];
				}

				if (strict)
					throw new EscapeException("Unknown escape sequence \"" + escapeChar + src.current() + "\"");
				else
					return src.consume();
			} else
				return src.consume();
		} catch (EscapeException | RuntimeException e) {
			src.setPosition(resetPos);
			throw e;
		}
	}

	public int getUnescapedLength(CharReader src, int length, CharPredicate until) {
		int end;
		if (length < 0)
			end = Integer.MAX_VALUE;
		else
			end = src.getPosition() + length;

		int result = 0;

		while (src.has() && src.getPosition() < end && (until == null || !until.test(src.current()))) {
			skipOneSequence(src);
			result++;
		}

		return result;
	}

	public void skipOneSequence(CharReader src) {
		if (src.current() == escapeChar && src.next() == unicodeEscapeChar) {
			src.advance(4);
		}
		src.next();
	}

	/*
	 * Utility
	 */

	public void escape(CharReader src, int length, CharConsumer output) {
		escape(src, length, null, output);
	}

	public void escape(CharReader src, CharPredicate until, CharConsumer output) {
		escape(src, -1, until, output);
	}

	public void escape(CharReader src, CharConsumer output) {
		escape(src, -1, null, output);
	}

	public int getEscapedLength(CharReader src, int length) {
		return getEscapedLength(src, length, null);
	}

	public int getEscapedLength(CharReader src, CharPredicate until) {
		return getEscapedLength(src, -1, until);
	}

	public int getEscapedLength(CharReader src) {
		return getEscapedLength(src, -1, null);
	}

	public char[] escape(CharReader src, int length, CharPredicate until) {
		src.mark();
		char[] result = new char[getEscapedLength(src, length, until)];
		src.reset();
		escape(src, length, until, CharConsumers.fillArray(result));
		return result;
	}

	public char[] escape(CharReader src, int length) {
		return escape(src, length, (CharPredicate) null);
	}

	public char[] escape(CharReader src, CharPredicate until) {
		return escape(src, -1, until);
	}

	public char[] escape(CharReader src) {
		return escape(src, -1, (CharPredicate) null);
	}

	public void unescape(CharReader src, int length, CharConsumer output) throws EscapeException {
		unescape(src, length, null, output);
	}

	public void unescape(CharReader src, CharPredicate until, CharConsumer output) throws EscapeException {
		unescape(src, -1, until, output);
	}

	public void unescape(CharReader src, CharConsumer output) throws EscapeException {
		unescape(src, -1, null, output);
	}

	public int getUnescapedLength(CharReader src, int length) {
		return getUnescapedLength(src, length, null);
	}

	public int getUnescapedLength(CharReader src, CharPredicate until) {
		return getUnescapedLength(src, -1, until);
	}

	public int getUnescapedLength(CharReader src) {
		return getUnescapedLength(src, -1, null);
	}

	public char[] unescape(CharReader src, int length, CharPredicate until) throws EscapeException {
		src.mark();
		char[] result = new char[getUnescapedLength(src, length, until)];
		src.reset();
		unescape(src, length, until, CharConsumers.fillArray(result));
		return result;
	}

	public char[] unescape(CharReader src, int length) throws EscapeException {
		return unescape(src, length, (CharPredicate) null);
	}

	public char[] unescape(CharReader src, CharPredicate until) throws EscapeException {
		return unescape(src, -1, until);
	}

	public char[] unescape(CharReader src) throws EscapeException {
		return unescape(src, -1, (CharPredicate) null);
	}

	@Deprecated()
	public char[] unescape(CharacterIterator src, char until) throws EscapeException {
		int index = src.getIndex();
		CharReader reader = CharReaders.wrap(src);

		char[] result = unescape(reader, -1, CharPredicate.forChar(until));

		src.setIndex(index + reader.getPosition());
		return result;
	}

	public String escape(String src) {
		StringBuilder result = new StringBuilder(src.length());
		escape(CharReaders.wrap(src), (CharConsumer) result::append);
		return result.toString();
	}

	public String unescape(String src) throws EscapeException {
		StringBuilder result = new StringBuilder(src.length());
		unescape(CharReaders.wrap(src), (CharConsumer) result::append);
		return result.toString();
	}

	/*
	 * Misc
	 */

	private static int hexValue(char c) throws EscapeException {
		if (c < '0')
			throw thisIsNotAHexDigit(c);
		if (c <= '9')
			return c - '0';
		if (c < 'A')
			throw thisIsNotAHexDigit(c);
		if (c <= 'F')
			return c - 'A';
		if (c < 'a')
			throw thisIsNotAHexDigit(c);
		if (c <= 'f')
			return c - 'a';
		if (c == CharReader.DONE)
			throw new EscapeException("Incomplete Unicode escape sequence at the end");
		throw thisIsNotAHexDigit(c);
	}

	private static EscapeException thisIsNotAHexDigit(char c) {
		return new EscapeException("Invalid hex digit '" + c + "', expected [0-9A-Fa-f]");
	}

	protected static boolean isRegular(char c) {
		return c >= ' ' && c <= '~';
	}

	/*
	 * Getters / setters
	 */

	public char getEscapeChar() {
		return escapeChar;
	}

	public char getUnicodeEscapeChar() {
		return unicodeEscapeChar;
	}

	public char[] getSafes() {
		return safes;
	}

	public char[] getUnsafes() {
		return unsafes;
	}

	public boolean isPreferUnicode() {
		return preferUnicode;
	}

	public boolean isStrict() {
		return strict;
	}

}
