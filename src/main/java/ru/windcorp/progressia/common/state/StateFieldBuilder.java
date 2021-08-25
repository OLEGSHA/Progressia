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

import java.util.function.Supplier;

import ru.windcorp.progressia.common.state.codec.ObjectCodec;
import ru.windcorp.progressia.common.state.codec.ObjectCodecRegistry;

public interface StateFieldBuilder {

	public static interface Int {
		IntStateField build();
	}
	
	public static interface Obj<T> {
		ObjectStateField<T> build();
	}

	Int ofInt();
	
	<T> Obj<T> of(ObjectCodec<T> codec, Supplier<T> defaultValue);
	
	default <T> Obj<T> of(Class<T> clazz, Supplier<T> defaultValue) {
		ObjectCodec<T> codec = ObjectCodecRegistry.get(clazz);
		return of(codec, defaultValue);
	}
	
	default <T> Obj<T> of(ObjectCodec<T> codec, T defaultValue) {
		return of(codec, (Supplier<T>) () -> codec.copy(defaultValue, null));
	}
	
	default <T> Obj<T> of(Class<T> clazz, T defaultValue) {
		ObjectCodec<T> codec = ObjectCodecRegistry.get(clazz);
		return of(codec, (Supplier<T>) () -> codec.copy(defaultValue, null));
	}
	
	default <T> Obj<T> of(ObjectCodec<T> codec) {
		return of(codec, (Supplier<T>) () -> null);
	}
	
	default <T> Obj<T> of(Class<T> clazz) {
		ObjectCodec<T> codec = ObjectCodecRegistry.get(clazz);
		return of(codec, (Supplier<T>) () -> null);
	}
	
	@SuppressWarnings("unchecked")
	default <T> Obj<T> def(Supplier<T> defaultValue) {
		Class<T> clazz = (Class<T>) defaultValue.get().getClass();
		return of(clazz, defaultValue);
	}
	
	@SuppressWarnings("unchecked")
	default <T> Obj<T> def(T defaultValue) {
		return of((Class<T>) defaultValue.getClass(), defaultValue);
	}

	StateFieldBuilder setLocal(boolean isLocal);

	default StateFieldBuilder setLocal() {
		return setLocal(true);
	}

	default StateFieldBuilder setShared() {
		return setLocal(false);
	}

	void setOrdinal(int ordinal);

}
