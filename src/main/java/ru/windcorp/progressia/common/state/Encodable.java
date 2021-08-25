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
package ru.windcorp.progressia.common.state;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface Encodable {

	/**
	 * Sets the state of this object according to the binary representation read
	 * from {@code input}. If {@code context == COMMS}, the state of local
	 * fields is unspecified after this operation.
	 * 
	 * @param input   a {@link DataInput} that a state can be read from
	 * @param context the context
	 * @throws IOException if the state is encoded poorly or an error occurs
	 *                     in {@code input}
	 */
	void read(DataInput input, IOContext context) throws IOException;

	/**
	 * Writes the binary representation of the state of this object to the
	 * {@code output}.
	 * 
	 * @param output  a {@link DataOutput} that a state can be written to
	 * @param context the context
	 * @throws IOException if an error occurs in {@code output}
	 */
	void write(DataOutput output, IOContext context) throws IOException;

	/**
	 * Turns {@code destination} into a deep copy of this object.
	 * <p>
	 * Changes the provided object so that:
	 * <ul>
	 * <li>the provided object equals this object; and</li>
	 * <li>the provided object is independent of this object, meaning no change
	 * to {@code destination} can affect this object.</li>
	 * </ul>
	 * 
	 * @param destination the object to copy this object into. Runtime class
	 *                    must match this class
	 * @return {@code destination}
	 */
	void copy(Encodable destination);

}
