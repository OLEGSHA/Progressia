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
 
package ru.windcorp.jputil;

public class ConstantsMapException extends RuntimeException {

	private static final long serialVersionUID = -4298704891780063127L;

	public ConstantsMapException() {

	}

	public ConstantsMapException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ConstantsMapException(String message, Throwable cause) {
		super(message, cause);
	}

	public ConstantsMapException(String message) {
		super(message);
	}

	public ConstantsMapException(Throwable cause) {
		super(cause);
	}

}
