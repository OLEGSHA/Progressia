/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
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

package ru.windcorp.progressia.client.localization;

import java.util.IllegalFormatException;

public class MutableStringFormatter extends MutableString {
	private final Object format;
	private final Object[] args;

	public MutableStringFormatter(Object format, Object[] args) {
		this.format = format;
		this.args = args;
		listen(format);

		for (Object arg : args) {
			listen(arg);
		}
	}

	@Override
	protected String compute() {
		String format = this.format.toString();
		try {
			return String.format(format, this.args);
		} catch (IllegalFormatException e) {
			return format;
		}
	}

}
