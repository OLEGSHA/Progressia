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
import java.io.IOException;

import ru.windcorp.progressia.common.state.IOContext;

public abstract class ImmutableObjectCodec<T> extends ObjectCodec<T> {

	public ImmutableObjectCodec(Class<T> clazz) {
		super(clazz);
	}
	
	@Override
	public final T copy(T object, T previous) {
		return object;
	}
	
	@Override
	protected final T doRead(T previous, DataInput input, IOContext context) throws IOException {
		return doRead(input, context);
	}

	protected abstract T doRead(DataInput input, IOContext context) throws IOException;

}
