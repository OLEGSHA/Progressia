package ru.windcorp.progressia.common.world.generic;

import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;

import glm.vec._3.i.Vec3i;
import gnu.trove.map.TLongObjectMap;
import ru.windcorp.progressia.common.util.CoordinatePacker;
import ru.windcorp.progressia.common.util.Vectors;

public class LongBasedChunkMap<V> implements ChunkMap<V> {
	
	protected final TLongObjectMap<V> impl;
	private final ChunkSet keys;

	public LongBasedChunkMap(TLongObjectMap<V> impl) {
		this.impl = impl;
		this.keys = new LongBasedChunkSet(impl.keySet());
	}

	private static long getKey(Vec3i v) {
		return CoordinatePacker.pack3IntsIntoLong(v);
	}
	
	private static Vec3i getVector(long key, Vec3i output) {
		return CoordinatePacker.unpack3IntsFromLong(key, output);
	}

	@Override
	public int size() {
		return impl.size();
	}

	@Override
	public boolean containsKey(Vec3i pos) {
		return impl.containsKey(getKey(pos));
	}

	@Override
	public V get(Vec3i pos) {
		return impl.get(getKey(pos));
	}

	@Override
	public V put(Vec3i pos, V obj) {
		return impl.put(getKey(pos), obj);
	}

	@Override
	public V remove(Vec3i pos) {
		return impl.remove(getKey(pos));
	}

	@Override
	public Collection<V> values() {
		return impl.valueCollection();
	}

	@Override
	public ChunkSet keys() {
		return keys;
	}

	@Override
	public boolean removeIf(BiPredicate<? super Vec3i, ? super V> condition) {
		Vec3i v = Vectors.grab3i();
		
		boolean result = impl.retainEntries((key, value) -> {
			return !condition.test(getVector(key, v), value);
		});
		
		Vectors.release(v);
		return result;
	}

	@Override
	public void forEach(BiConsumer<? super Vec3i, ? super V> action) {
		Vec3i v = Vectors.grab3i();
		
		impl.forEachEntry((key, value) -> {
			action.accept(getVector(key, v), value);
			return true;
		});
		
		Vectors.release(v);
	}

}
