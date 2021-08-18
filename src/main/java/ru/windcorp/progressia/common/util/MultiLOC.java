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

package ru.windcorp.progressia.common.util;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MultiLOC {

	private final Map<Class<?>, LowOverheadCache<?>> caches = new HashMap<>();

	public <T> MultiLOC addClass(Class<T> clazz, Supplier<T> generator) {
		caches.put(clazz, new LowOverheadCache<>(generator));
		return this;
	}

	public <T> T grab(Class<T> clazz) {
		return clazz.cast(caches.get(clazz).grab());
	}

	@SuppressWarnings("unchecked")
	public void release(Object obj) {
		((LowOverheadCache<Object>) caches.get(obj.getClass())).release(obj);
	}

}
