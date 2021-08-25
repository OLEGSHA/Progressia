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

import ru.windcorp.progressia.common.state.IOContext;

public abstract class ObjectCodec<T> {

	private final Class<T> clazz;

	public ObjectCodec(Class<T> clazz) {
		this.clazz = clazz;
	}

	public Class<T> getDataType() {
		return clazz;
	}

	@SuppressWarnings("unchecked")
	public final T read(Object previous, DataInput input, IOContext context) throws IOException {
		assert previous == null || clazz.isInstance(previous)
			: "Cannot use codec " + this + " on object of type " + previous.getClass();
		
		T result = doRead((T) previous, input, context);
		
		assert result == null || clazz.isInstance(previous)
			: "Codec " + this + " read object of type " + previous.getClass();
		return result;
	}

	protected abstract T doRead(T previous, DataInput input, IOContext context) throws IOException;

	@SuppressWarnings("unchecked")
	public final void write(Object value, DataOutput output, IOContext context) throws IOException {
		assert value == null || clazz.isInstance(value)
			: "Cannot use codec " + this + " on object of type " + value.getClass();
		
		doWrite((T) value, output, context);
	}

	protected abstract void doWrite(T obj, DataOutput output, IOContext context) throws IOException;

	public abstract T copy(T object, T previous);

	public int computeHashCode(T object) {
		return Objects.hashCode(object);
	}

	public boolean areEqual(T a, T b) {
		return Objects.equals(a, b);
	}

}
