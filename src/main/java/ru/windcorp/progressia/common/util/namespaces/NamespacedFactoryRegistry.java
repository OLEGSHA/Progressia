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

package ru.windcorp.progressia.common.util.namespaces;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class NamespacedFactoryRegistry<E extends Namespaced>
		implements Map<String, NamespacedFactoryRegistry.Factory<E>> {

	@FunctionalInterface
	public static interface Factory<E> {
		E build(String id);
	}

	private final Map<String, Factory<E>> backingMap = Collections.synchronizedMap(new HashMap<>());

	private final Logger logger = LogManager.getLogger(getClass());

	public void register(String id, Factory<E> element) {
		if (get(id) != null) {
			throw new IllegalArgumentException("ID " + id + " is already registered in " + getClass().getSimpleName());
		}

		logger.debug("Registering {} in {}", id, getClass().getSimpleName());
		backingMap.put(id, element);
	}

	@Override
	public int size() {
		return backingMap.size();
	}

	@Override
	public boolean isEmpty() {
		return backingMap.isEmpty();
	}

	@Override
	public boolean containsKey(Object key) {
		return backingMap.containsKey(key);
	}

	public boolean has(String id) {
		return backingMap.containsKey(id);
	}

	@Override
	public boolean containsValue(Object value) {
		return backingMap.containsValue(value);
	}

	public boolean isRegistered(E element) {
		return has(element.getId());
	}

	@Override
	public Factory<E> get(Object key) {
		return backingMap.get(key);
	}

	public E create(String id) {
		Factory<E> factory = get(id);
		E result = factory.build(id);
		if (!result.getId().equals(id)) {
			throw new IllegalStateException(
					"Requested ID " + id + " but factory " + factory + " returned an object with ID " + result.getId());
		}
		return result;
	}

	@Override
	public Factory<E> put(String id, Factory<E> factory) {
		register(id, factory);
		return null;
	}

	@Override
	public void putAll(Map<? extends String, ? extends Factory<E>> m) {
		synchronized (backingMap) {
			m.entrySet().forEach(e -> register(e.getKey(), e.getValue()));
		}
	}

	@Override
	public Factory<E> remove(Object key) {
		return backingMap.remove(key);
	}

	@Override
	public void clear() {
		backingMap.clear();
	}

	@Override
	public Set<String> keySet() {
		return backingMap.keySet();
	}

	@Override
	public Collection<Factory<E>> values() {
		return backingMap.values();
	}

	@Override
	public Set<Entry<String, Factory<E>>> entrySet() {
		return backingMap.entrySet();
	}

}
