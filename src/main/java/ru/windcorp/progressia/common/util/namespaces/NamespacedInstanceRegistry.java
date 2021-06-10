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

import com.google.errorprone.annotations.DoNotCall;

public class NamespacedInstanceRegistry<E extends Namespaced> implements Map<String, E> {

	private final Map<String, E> backingMap = Collections.synchronizedMap(new HashMap<>());

	private final Logger logger = LogManager.getLogger(getClass());

	public void register(E element) {
		logger.debug("Registering {} in {}", element.getId(), getClass().getSimpleName());
		backingMap.put(element.getId(), element);
	}

	public void registerAll(Collection<? extends E> elements) {
		for (E element : elements) {
			register(element);
		}
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
	public E get(Object key) {
		return backingMap.get(key);
	}

	/**
	 * Use {@link #register(E)}.
	 */
	@Override
	@DoNotCall
	@Deprecated
	public E put(String key, E value) {
		throw new UnsupportedOperationException("Use NamespacedInstanceRegistry.register(E)");
	}

	@Override
	public E remove(Object key) {
		return backingMap.remove(key);
	}

	/**
	 * Use {@link #registerAll(Collection)}.
	 */
	@Override
	@DoNotCall
	@Deprecated
	public void putAll(Map<? extends String, ? extends E> m) {
		throw new UnsupportedOperationException("Use NamespacedInstanceRegistry.registerAll(Collection<? extends E>)");
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
	public Collection<E> values() {
		return backingMap.values();
	}

	@Override
	public Set<Entry<String, E>> entrySet() {
		return backingMap.entrySet();
	}

}
