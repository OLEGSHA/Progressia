package ru.windcorp.progressia.common.util;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;

import com.google.errorprone.annotations.DoNotCall;

public class NamespacedRegistry<E extends Namespaced>
implements Map<String, E> {
	
	private final Map<String, E> backingMap =
			Collections.synchronizedMap(new HashMap<>());
	
	public void register(E element) {
		LogManager.getLogger(getClass()).debug("Registering " + element.getId());
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
	@DoNotCall @Deprecated
	public E put(String key, E value) {
		throw new UnsupportedOperationException(
				"Use NamespacedRegistry.register(E)"
		);
	}

	@Override
	public E remove(Object key) {
		return backingMap.remove(key);
	}

	/**
	 * Use {@link #registerAll(Collection)}.
	 */
	@Override
	@DoNotCall @Deprecated
	public void putAll(Map<? extends String, ? extends E> m) {
		throw new UnsupportedOperationException(
				"Use NamespacedRegistry.registerAll(Collection<? extends E>)"
		);
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
