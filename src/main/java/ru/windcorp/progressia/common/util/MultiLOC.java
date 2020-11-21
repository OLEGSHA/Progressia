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
