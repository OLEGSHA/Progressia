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

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

import glm.vec._3.i.Vec3i;
import gnu.trove.set.hash.TLongHashSet;

public class ChunkSets {

	public static ChunkSet newHashSet() {
		return new LongBasedChunkSet(new TLongHashSet());
	}

	public static ChunkSet newSyncHashSet(Object mutex) {
		return new SynchronizedChunkSet(new LongBasedChunkSet(new TLongHashSet()), mutex);
	}

	public static ChunkSet newSyncHashSet() {
		return newSyncHashSet(null);
	}

	public static ChunkSet empty() {
		return EMPTY_SET;
	}

	private ChunkSets() {
	}

	private final static ChunkSet EMPTY_SET = new ChunkSet() {

		@Override
		public Iterator<Vec3i> iterator() {
			return new Iterator<Vec3i>() {
				@Override
				public boolean hasNext() {
					return false;
				}

				@Override
				public Vec3i next() {
					throw new NoSuchElementException();
				}
			};
		}

		@Override
		public int size() {
			return 0;
		}

		@Override
		public boolean contains(Vec3i pos) {
			return false;
		}

		@Override
		public boolean add(Vec3i pos) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean remove(Vec3i pos) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean containsAll(ChunkSet other) {
			return false;
		}

		@Override
		public boolean containsAny(ChunkSet other) {
			return false;
		}

		@Override
		public void addAll(ChunkSet other) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void removeAll(ChunkSet other) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void retainAll(ChunkSet other) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void clear() {
			throw new UnsupportedOperationException();
		}

	};

	private static class SynchronizedChunkSet implements ChunkSet {

		private final ChunkSet parent;
		private final Object mutex;

		public SynchronizedChunkSet(ChunkSet parent, Object mutex) {
			Objects.requireNonNull(parent, "parent");
			this.parent = parent;

			this.mutex = mutex == null ? this : mutex;
		}

		@Override
		public Iterator<Vec3i> iterator() {
			return parent.iterator(); // Must be synchronized manually by user!
		}

		@Override
		public void forEach(Consumer<? super Vec3i> action) {
			synchronized (mutex) {
				parent.forEach(action);
			}
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
		public boolean contains(Vec3i pos) {
			synchronized (mutex) {
				return parent.contains(pos);
			}
		}

		@Override
		public boolean add(Vec3i pos) {
			synchronized (mutex) {
				return parent.add(pos);
			}
		}

		@Override
		public boolean remove(Vec3i pos) {
			synchronized (mutex) {
				return parent.remove(pos);
			}
		}

		@Override
		public boolean contains(int x, int y, int z) {
			synchronized (mutex) {
				return parent.contains(x, y, z);
			}
		}

		@Override
		public boolean add(int x, int y, int z) {
			synchronized (mutex) {
				return parent.add(x, y, z);
			}
		}

		@Override
		public boolean remove(int x, int y, int z) {
			synchronized (mutex) {
				return parent.remove(x, y, z);
			}
		}

		@Override
		public boolean contains(GenericChunk<?, ?, ?, ?> chunk) {
			synchronized (mutex) {
				return parent.contains(chunk);
			}
		}

		@Override
		public boolean add(GenericChunk<?, ?, ?, ?> chunk) {
			synchronized (mutex) {
				return parent.add(chunk);
			}
		}

		@Override
		public boolean remove(GenericChunk<?, ?, ?, ?> chunk) {
			synchronized (mutex) {
				return parent.remove(chunk);
			}
		}

		@Override
		public <C extends GenericChunk<C, ?, ?, ?>> void forEachIn(GenericWorld<?, ?, ?, C, ?> world,
				Consumer<? super C> action) {
			synchronized (mutex) {
				parent.forEachIn(world, action);
			}
		}

		@Override
		public boolean containsAll(ChunkSet other) {
			synchronized (mutex) {
				return parent.containsAll(other);
			}
		}

		@Override
		public boolean containsAny(ChunkSet other) {
			synchronized (mutex) {
				return parent.containsAny(other);
			}
		}

		@Override
		public void addAll(ChunkSet other) {
			synchronized (mutex) {
				parent.addAll(other);
			}
		}

		@Override
		public void removeAll(ChunkSet other) {
			synchronized (mutex) {
				parent.removeAll(other);
			}
		}

		@Override
		public void retainAll(ChunkSet other) {
			synchronized (mutex) {
				parent.retainAll(other);
			}
		}

		@Override
		public void clear() {
			synchronized (mutex) {
				parent.clear();
			}
		}

		@Override
		public boolean containsAll(Iterable<? extends Vec3i> other) {
			synchronized (mutex) {
				return parent.containsAll(other);
			}
		}

		@Override
		public boolean containsAny(Iterable<? extends Vec3i> other) {
			synchronized (mutex) {
				return parent.containsAny(other);
			}
		}

		@Override
		public void addAll(Iterable<? extends Vec3i> other) {
			synchronized (mutex) {
				parent.addAll(other);
			}
		}

		@Override
		public void removeAll(Iterable<? extends Vec3i> other) {
			synchronized (mutex) {
				parent.removeAll(other);
			}
		}

		@Override
		public void retainAll(Iterable<? extends Vec3i> other) {
			synchronized (mutex) {
				parent.retainAll(other);
			}
		}

		@Override
		public void removeIf(Predicate<? super Vec3i> condition) {
			synchronized (mutex) {
				parent.removeIf(condition);
			}
		}

		@Override
		public void retainIf(Predicate<? super Vec3i> condition) {
			synchronized (mutex) {
				parent.retainIf(condition);
			}
		}

		@Override
		public boolean containsAllChunks(Iterable<? extends GenericChunk<?, ?, ?, ?>> chunks) {
			synchronized (mutex) {
				return parent.containsAllChunks(chunks);
			}
		}

		@Override
		public boolean containsAnyChunks(Iterable<? extends GenericChunk<?, ?, ?, ?>> chunks) {
			synchronized (mutex) {
				return parent.containsAnyChunks(chunks);
			}
		}

		@Override
		public void addAllChunks(Iterable<? extends GenericChunk<?, ?, ?, ?>> chunks) {
			synchronized (mutex) {
				parent.addAllChunks(chunks);
			}
		}

		@Override
		public void removeAllChunks(Iterable<? extends GenericChunk<?, ?, ?, ?>> chunks) {
			synchronized (mutex) {
				parent.removeAllChunks(chunks);
			}
		}

	}

}
