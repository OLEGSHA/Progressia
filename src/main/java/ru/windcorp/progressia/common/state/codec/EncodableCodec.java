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
package ru.windcorp.progressia.common.state.codec;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Objects;

import ru.windcorp.progressia.common.state.Encodable;
import ru.windcorp.progressia.common.state.IOContext;

public class EncodableCodec extends ReusableObjectCodec<Encodable> {

	public EncodableCodec() {
		super(Encodable.class);
	}
	
	@Override
	protected Encodable doRead(Encodable previous, DataInput input, IOContext context) throws IOException {
		previous.read(input, context);
		return previous;
	}
	
	@Override
	protected void doWrite(Encodable obj, DataOutput output, IOContext context) throws IOException {
		obj.write(output, context);
	}
	
	@Override
	protected Encodable doCopy(Encodable object, Encodable previous) {
		Objects.requireNonNull(previous, "previous");
		object.copy(previous);
		return previous;
	}

}
