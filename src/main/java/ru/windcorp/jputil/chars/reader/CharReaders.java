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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.CharacterIterator;
import java.util.function.IntSupplier;

import ru.windcorp.jputil.chars.CharSupplier;

/**
 * @author Javapony
 */
public class CharReaders {

	private CharReaders() {
	}

	public static CharReader wrap(char[] array, int offset, int length) {
		return new ArrayCharReader(array, offset, length);
	}

	public static CharReader wrap(char[] array) {
		return wrap(array, 0, array.length);
	}

	public static CharReader wrap(String str, int offset, int length) {
		return new StringCharReader(str, offset, length);
	}

	public static CharReader wrap(String str) {
		return wrap(str, 0, str.length());
	}

	public static CharReader wrap(CharSupplier supplier) {
		return new BufferedCharReader() {
			@Override
			protected char pullChar() {
				try {
					return supplier.getAsChar();
				} catch (Exception e) {
					return DONE;
				}
			}
		};
	}

	public static CharReader wrap(IntSupplier supplier) {
		return new BufferedCharReader() {
			@Override
			protected char pullChar() {
				try {
					int i = supplier.getAsInt();
					if (i < 0 || i > Character.MAX_VALUE) {
						return DONE;
					} else {
						return (char) i;
					}
				} catch (Exception e) {
					return DONE;
				}
			}
		};
	}

	public static CharReader wrap(CharacterIterator it) {
		return new BufferedCharReader() {
			@Override
			protected char pullChar() {
				char result = it.current();
				it.next();
				return result;
			}
		};
	}

	public static CharReader wrap(Reader reader) {
		return new ReaderCharReader(reader);
	}

	public static CharReader wrap(InputStream is, Charset charset) {
		return wrap(new InputStreamReader(is, charset));
	}

	public static CharReader wrapDefaultCS(InputStream is) {
		return wrap(new InputStreamReader(is));
	}

	public static CharReader wrapUTF8(InputStream is) {
		return wrap(new InputStreamReader(is, StandardCharsets.UTF_8));
	}

}
