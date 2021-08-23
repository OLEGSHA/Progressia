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

public class UncheckedEscapeException extends RuntimeException {

	private static final long serialVersionUID = 5392628641744570926L;

	public UncheckedEscapeException(String message, EscapeException cause) {
		super(message, cause);
	}

	public UncheckedEscapeException(EscapeException cause) {
		super(cause);
	}

	@Override
	public synchronized EscapeException getCause() {
		return (EscapeException) super.getCause();
	}

}
