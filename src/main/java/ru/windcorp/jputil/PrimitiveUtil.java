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

import java.util.HashMap;
import java.util.Map;

public class PrimitiveUtil {

	private PrimitiveUtil() {
	}

	private static final Map<Class<?>, Class<?>> PRIMITIVE_TO_BOXED = new HashMap<>();
	private static final Map<Class<?>, Object> PRIMITIVE_TO_NULL = new HashMap<>();

	static {
		for (Class<?> boxed : new Class<?>[] { Boolean.class, Byte.class, Short.class, Character.class, Integer.class,
				Long.class, Float.class, Double.class }) {
			try {
				PRIMITIVE_TO_BOXED.put((Class<?>) boxed.getField("TYPE").get(null), boxed);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		PRIMITIVE_TO_NULL.put(Boolean.TYPE, Boolean.FALSE);
		PRIMITIVE_TO_NULL.put(Byte.TYPE, Byte.valueOf((byte) 0));
		PRIMITIVE_TO_NULL.put(Short.TYPE, Short.valueOf((short) 0));
		PRIMITIVE_TO_NULL.put(Integer.TYPE, Integer.valueOf(0));
		PRIMITIVE_TO_NULL.put(Long.TYPE, Long.valueOf(0));
		PRIMITIVE_TO_NULL.put(Float.TYPE, Float.valueOf(Float.NaN));
		PRIMITIVE_TO_NULL.put(Double.TYPE, Double.valueOf(Double.NaN));
		PRIMITIVE_TO_NULL.put(Character.TYPE, Character.valueOf('\u0000'));
	}

	public static Class<?> getBoxedClass(Class<?> primitiveClass) {
		return PRIMITIVE_TO_BOXED.getOrDefault(primitiveClass, primitiveClass);
	}

	public static Object getPrimitiveNull(Class<?> primitiveClass) {
		return PRIMITIVE_TO_NULL.get(primitiveClass);
	}

}
