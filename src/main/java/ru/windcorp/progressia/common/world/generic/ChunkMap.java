package ru.windcorp.progressia.common.world.generic;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import glm.vec._3.i.Vec3i;

public interface ChunkMap<V> {
	
	/*
	 * Size
	 */
	
	int size();
	
	default boolean isEmpty() {
		return size() == 0;
	}
	
	/*
	 * Basic operations
	 */
	
	boolean containsKey(Vec3i pos);
	
	V get(Vec3i pos);
	V put(Vec3i pos, V obj);
	V remove(Vec3i pos);
	
	default boolean containsValue(V value) {
		return values().contains(value);
	}
	
	default V getOrDefault(Vec3i pos, V def) {
		return containsKey(pos) ? def : get(pos);
	}
	
	default V compute(Vec3i pos, BiFunction<? super Vec3i, ? super V, ? extends V> remappingFunction) {
		V newValue = remappingFunction.apply(pos, get(pos));
		
		if (newValue == null) {
			remove(pos);
		} else {
			put(pos, newValue);
		}
		
		return newValue;
	}
	
	// TODO implement ALL default methods from Map
	
	/*
	 * Basic operation wrappers
	 */
	
	// TODO implement (int, int, int) and GenericChunk versions of all of the above
	
	default boolean containsChunk(GenericChunk<?, ?, ?, ?> chunk) {
		return containsKey(chunk.getPosition());
	}
	
	default V get(GenericChunk<?, ?, ?, ?> chunk) {
		return get(chunk.getPosition());
	}
	
	default V put(GenericChunk<?, ?, ?, ?> chunk, V obj) {
		return put(chunk.getPosition(), obj);
	}
	
	default V remove(GenericChunk<?, ?, ?, ?> chunk) {
		return remove(chunk.getPosition());
	}
	
	default V getOrDefault(GenericChunk<?, ?, ?, ?> chunk, V def) {
		return containsChunk(chunk) ? def : get(chunk);
	}
	
	default <C extends GenericChunk<C, ?, ?, ?>> V compute(C chunk, BiFunction<? super C, ? super V, ? extends V> remappingFunction) {
		V newValue = remappingFunction.apply(chunk, get(chunk));
		
		if (newValue == null) {
			remove(chunk);
		} else {
			put(chunk, newValue);
		}
		
		return newValue;
	}
	
	/*
	 * Views
	 */
	
	Collection<V> values();
	ChunkSet keys();
	
	/*
	 * Bulk operations
	 */
	
	boolean removeIf(BiPredicate<? super Vec3i, ? super V> condition);
	void forEach(BiConsumer<? super Vec3i, ? super V> action);
	
	default <C extends GenericChunk<C, ?, ?, ?>> void forEachIn(GenericWorld<?, ?, ?, C, ?> world, BiConsumer<? super C, ? super V> action) {
		forEach((pos, value) -> {
			C chunk = world.getChunk(pos);
			if (chunk == null) return;
			action.accept(chunk, value);
		});
	}

}
