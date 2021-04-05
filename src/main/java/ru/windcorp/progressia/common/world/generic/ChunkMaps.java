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
 
package ru.windcorp.progressia.common.world.generic;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import glm.vec._3.i.Vec3i;
import gnu.trove.map.hash.TLongObjectHashMap;

public class ChunkMaps {

	public static <V> ChunkMap<V> newHashMap() {
		return new LongBasedChunkMap<V>(new TLongObjectHashMap<V>());
	}

	public static <V> ChunkMap<V> newSyncHashMap(Object mutex) {
		return new SynchronizedChunkMap<V>(new LongBasedChunkMap<V>(new TLongObjectHashMap<V>()), mutex);
	}

	public static <V> ChunkMap<V> newSyncHashMap() {
		return newSyncHashMap(null);
	}

	@SuppressWarnings("unchecked")
	public static <V> ChunkMap<V> empty() {
		return (ChunkMap<V>) EMPTY_MAP;
	}

	private ChunkMaps() {
	}

	private final static ChunkMap<Object> EMPTY_MAP = new ChunkMap<Object>() {

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean containsKey(Vec3i pos) {
			return false;
		}

		@Override
		public Object get(Vec3i pos) {
			return null;
		}

		@Override
		public Object put(Vec3i pos, Object obj) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Object remove(Vec3i pos) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Collection<Object> values() {
			return Collections.emptyList();
		}

		@Override
		public ChunkSet keys() {
			return ChunkSets.empty();
		}

		@Override
		public boolean removeIf(BiPredicate<? super Vec3i, ? super Object> condition) {
			return false;
		}

		@Override
		public void forEach(BiConsumer<? super Vec3i, ? super Object> action) {
			// Do nothing	
		}

	};

	private static class SynchronizedChunkMap<V> implements ChunkMap<V> {

		private final ChunkMap<V> parent;
		private final Object mutex;

		public SynchronizedChunkMap(ChunkMap<V> parent, Object mutex) {
			Objects.requireNonNull(parent, "parent");
			this.parent = parent;

			this.mutex = mutex == null ? this : mutex;
		}

		@Override
		public int size() {
			synchronized (mutex) {
				return parent.size();
			}
		}

		@Override
		public boolean isEmpty() {
			synchronized (mutex) {
				return parent.isEmpty();
			}
		}

		@Override
		public boolean containsKey(Vec3i pos) {
			synchronized (mutex) {
				return parent.containsKey(pos);
			}
		}

		@Override
		public V get(Vec3i pos) {
			synchronized (mutex) {
				return parent.get(pos);
			}
		}

		@Override
		public V put(Vec3i pos, V obj) {
			synchronized (mutex) {
				return parent.put(pos, obj);
			}
		}

		@Override
		public V remove(Vec3i pos) {
			synchronized (mutex) {
				return parent.remove(pos);
			}
		}

		@Override
		public boolean containsValue(V value) {
			synchronized (mutex) {
				return parent.containsValue(value);
			}
		}

		@Override
		public V getOrDefault(Vec3i pos, V def) {
			synchronized (mutex) {
				return parent.getOrDefault(pos, def);
			}
		}

		@Override
		public V compute(Vec3i pos, BiFunction<? super Vec3i, ? super V, ? extends V> remappingFunction) {
			synchronized (mutex) {
				return parent.compute(pos, remappingFunction);
			}
		}

		@Override
		public boolean containsChunk(GenericChunk<?, ?, ?, ?, ?> chunk) {
			synchronized (mutex) {
				return parent.containsChunk(chunk);
			}
		}

		@Override
		public V get(GenericChunk<?, ?, ?, ?, ?> chunk) {
			synchronized (mutex) {
				return parent.get(chunk);
			}
		}

		@Override
		public V put(GenericChunk<?, ?, ?, ?, ?> chunk, V obj) {
			synchronized (mutex) {
				return parent.put(chunk, obj);
			}
		}

		@Override
		public V remove(GenericChunk<?, ?, ?, ?, ?> chunk) {
			synchronized (mutex) {
				return parent.remove(chunk);
			}
		}

		@Override
		public V getOrDefault(GenericChunk<?, ?, ?, ?, ?> chunk, V def) {
			synchronized (mutex) {
				return parent.getOrDefault(chunk, def);
			}
		}

		@Override
		public <C extends GenericChunk<?, ?, ?, ?, C>> V compute(
			C chunk,
			BiFunction<? super C, ? super V, ? extends V> remappingFunction
		) {
			synchronized (mutex) {
				return parent.compute(chunk, remappingFunction);
			}
		}

		@Override
		public Collection<V> values() {
			synchronized (mutex) {
				return parent.values();
			}
		}

		@Override
		public ChunkSet keys() {
			synchronized (mutex) {
				return parent.keys();
			}
		}

		@Override
		public boolean removeIf(BiPredicate<? super Vec3i, ? super V> condition) {
			synchronized (mutex) {
				return parent.removeIf(condition);
			}
		}

		@Override
		public void forEach(BiConsumer<? super Vec3i, ? super V> action) {
			synchronized (mutex) {
				parent.forEach(action);
			}
		}

		@Override
		public <C extends GenericChunk<?, ?, ?, ?, C>> void forEachIn(
			GenericWorld<?, ?, ?, ?, C, ?> world,
			BiConsumer<? super C, ? super V> action
		) {
			synchronized (mutex) {
				parent.forEachIn(world, action);
			}
		}

	}

}
