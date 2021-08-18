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

import java.util.function.IntSupplier;

@FunctionalInterface
public interface CharSupplier {

	char getAsChar();

	public static IntSupplier toInt(CharSupplier consumer) {
		return consumer::getAsChar;
	}

	public static CharSupplier toChar(IntSupplier consumer) {
		return () -> (char) consumer.getAsInt();
	}

}
