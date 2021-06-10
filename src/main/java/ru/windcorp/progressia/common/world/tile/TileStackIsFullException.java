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

package ru.windcorp.progressia.common.world.tile;

public class TileStackIsFullException extends RuntimeException {

	private static final long serialVersionUID = 6665942370305610231L;

	public TileStackIsFullException() {

	}

	public TileStackIsFullException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TileStackIsFullException(String message, Throwable cause) {
		super(message, cause);
	}

	public TileStackIsFullException(String message) {
		super(message);
	}

	public TileStackIsFullException(Throwable cause) {
		super(cause);
	}

}
