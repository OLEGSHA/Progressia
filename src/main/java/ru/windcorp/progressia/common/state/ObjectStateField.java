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
import java.util.function.Supplier;

import ru.windcorp.progressia.common.state.codec.ObjectCodec;

public class ObjectStateField<T> extends StateField {
	
	private final ObjectCodec<T> codec;
	private final Supplier<T> defaultValue;

	public ObjectStateField(
		String id,
		boolean isLocal,
		int index,
		ObjectCodec<T> codec,
		Supplier<T> defaultValue
	) {
		super(id, isLocal, index);
		
		this.codec = codec;
		this.defaultValue = defaultValue;
	}
	
	public ObjectCodec<T> getCodec() {
		return codec;
	}

	@SuppressWarnings("unchecked")
	public T get(StatefulObject object) {
		return (T) object.getStorage().getObject(getIndex());
	}

	public void setNow(StatefulObject object, T value) {
		object.getStorage().setObject(getIndex(), value);
	}

	public void set(StateChanger changer, T value) {
		changer.setObject(this, value);
	}

	@Override
	public void read(
		StatefulObject object,
		DataInput input,
		IOContext context
	)
		throws IOException {
		
		T previous = get(object);
		T result = codec.read(previous, input, context);
		object.getStorage().setObject(getIndex(), result);
	}

	@Override
	public void write(
		StatefulObject object,
		DataOutput output,
		IOContext context
	)
		throws IOException {
		
		codec.write(object.getStorage().getObject(getIndex()), output, context);
	}

	@Override
	public void copy(StatefulObject from, StatefulObject to) {
		setNow(to, get(from));
	}

	@Override
	public int computeHashCode(StatefulObject object) {
		return codec.computeHashCode(get(object));
	}

	@Override
	public boolean areEqual(StatefulObject a, StatefulObject b) {
		return codec.areEqual(get(a), get(b));
	}
	
	@Override
	public void setDefault(StateStorage storage) {
		storage.setObject(getIndex(), defaultValue.get());
	}

}
