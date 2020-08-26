package ru.windcorp.progressia.common.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class LowOverheadCache<E> {
	
	private final ThreadLocal<List<E>> ready =
			ThreadLocal.withInitial(ArrayList::new);
	
	private final Supplier<E> generator;
	
	public LowOverheadCache(Supplier<E> generator) {
		this.generator = generator;
	}

	public E grab() {
		List<E> list = ready.get();
		int size = list.size();
		
		if (size == 0) {
			return generator.get();
		}
		
		return list.remove(size - 1);
	}
	
	public void release(E object) {
		ready.get().add(object);
	}

}
