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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import ru.windcorp.progressia.common.state.Encodable;

public class ObjectCodecRegistry {
	
	private static final Map<Class<?>, ObjectCodec<?>> CODECS = Collections.synchronizedMap(new HashMap<>());
	private static final EncodableCodec ENCODABLE_FALLBACK = new EncodableCodec();
	
	public static void register(ObjectCodec<?> codec) {
		CODECS.put(codec.getDataType(), codec);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> ObjectCodec<T> get(Class<T> clazz) {
		ObjectCodec<?> codec = CODECS.get(clazz);
		if (codec != null) {
			return (ObjectCodec<T>) codec;
		}
		
		if (Encodable.class.isAssignableFrom(clazz)) {
			return (ObjectCodec<T>) ENCODABLE_FALLBACK;
		}
		
		throw new IllegalArgumentException("No codec registered for class " + clazz);
	}

}
