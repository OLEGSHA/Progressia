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
import java.util.Iterator;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

import glm.vec._3.i.Vec3i;
import gnu.trove.set.hash.TLongHashSet;
import ru.windcorp.progressia.common.util.Vectors;

public interface ChunkSet extends Iterable<Vec3i> {

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

	boolean contains(Vec3i pos);

	boolean add(Vec3i pos);

	boolean remove(Vec3i pos);

	/*
	 * Basic operation wrappers
	 */

	default boolean contains(int x, int y, int z) {
		Vec3i v = Vectors.grab3i();
		boolean result = contains(v);
		Vectors.release(v);
		return result;
	}

	default boolean add(int x, int y, int z) {
		Vec3i v = Vectors.grab3i();
		boolean result = add(v);
		Vectors.release(v);
		return result;
	}

	default boolean remove(int x, int y, int z) {
		Vec3i v = Vectors.grab3i();
		boolean result = remove(v);
		Vectors.release(v);
		return result;
	}

	default boolean contains(GenericChunk<?, ?, ?, ?> chunk) {
		return contains(chunk.getPosition());
	}

	default boolean add(GenericChunk<?, ?, ?, ?> chunk) {
		return add(chunk.getPosition());
	}

	default boolean remove(GenericChunk<?, ?, ?, ?> chunk) {
		return remove(chunk.getPosition());
	}

	default <C extends GenericChunk<C, ?, ?, ?>> void forEachIn(GenericWorld<?, ?, ?, C, ?> world,
			Consumer<? super C> action) {
		forEach(position -> {
			C chunk = world.getChunk(position);
			if (chunk == null)
				return;
			action.accept(chunk);
		});
	}

	/*
	 * Bulk operations on ChunkSets
	 */

	boolean containsAll(ChunkSet other);

	boolean containsAny(ChunkSet other);

	void addAll(ChunkSet other);

	void removeAll(ChunkSet other);

	void retainAll(ChunkSet other);

	/*
	 * Other bulk operations
	 */

	void clear();

	default boolean containsAll(Iterable<? extends Vec3i> other) {
		boolean[] hasMissing = new boolean[] { false };

		other.forEach(v -> {
			if (!contains(v)) {
				hasMissing[0] = true;
			}
		});

		return hasMissing[0];
	}

	default boolean containsAny(Iterable<? extends Vec3i> other) {
		boolean[] hasPresent = new boolean[] { false };

		other.forEach(v -> {
			if (contains(v)) {
				hasPresent[0] = true;
			}
		});

		return hasPresent[0];
	}

	default void addAll(Iterable<? extends Vec3i> other) {
		other.forEach(this::add);
	}

	default void removeAll(Iterable<? extends Vec3i> other) {
		other.forEach(this::remove);
	}

	default void retainAll(Iterable<? extends Vec3i> other) {
		if (other instanceof ChunkSet) {
			// We shouldn't invoke retainAll(ChunkSet) because we could be the
			// fallback for it
			removeIf(v -> !((ChunkSet) other).contains(v));
			return;
		}

		final int threshold = 16; // Maximum size of other at which point
									// creating a Set becomes faster than
									// iterating

		Collection<? extends Vec3i> collection = null;
		int otherSize = -1;

		if (other instanceof Set<?>) {
			collection = (Set<? extends Vec3i>) other;
		} else if (other instanceof Collection<?>) {
			Collection<? extends Vec3i> otherAsCollection = ((Collection<? extends Vec3i>) other);
			otherSize = otherAsCollection.size();

			if (otherSize < threshold) {
				collection = otherAsCollection;
			}
		}

		if (collection != null) {
			final Collection<? extends Vec3i> c = collection;
			removeIf(v -> !c.contains(v));
			return;
		}

		if (otherSize < 0) {
			otherSize = gnu.trove.impl.Constants.DEFAULT_CAPACITY;
		}

		retainAll(new LongBasedChunkSet(new TLongHashSet(otherSize), other));
		return;
	}

	default void removeIf(Predicate<? super Vec3i> condition) {
		for (Iterator<? extends Vec3i> it = iterator(); it.hasNext();) {
			if (condition.test(it.next())) {
				it.remove();
			}
		}
	}

	default void retainIf(Predicate<? super Vec3i> condition) {
		for (Iterator<? extends Vec3i> it = iterator(); it.hasNext();) {
			if (!condition.test(it.next())) {
				it.remove();
			}
		}
	}

	default boolean containsAllChunks(Iterable<? extends GenericChunk<?, ?, ?, ?>> chunks) {
		boolean[] hasMissing = new boolean[] { false };

		chunks.forEach(c -> {
			if (!contains(c.getPosition())) {
				hasMissing[0] = true;
			}
		});

		return hasMissing[0];
	}

	default boolean containsAnyChunks(Iterable<? extends GenericChunk<?, ?, ?, ?>> chunks) {
		boolean[] hasPresent = new boolean[] { false };

		chunks.forEach(c -> {
			if (contains(c.getPosition())) {
				hasPresent[0] = true;
			}
		});

		return hasPresent[0];
	}

	default void addAllChunks(Iterable<? extends GenericChunk<?, ?, ?, ?>> chunks) {
		chunks.forEach(this::add);
	}

	default void removeAllChunks(Iterable<? extends GenericChunk<?, ?, ?, ?>> chunks) {
		chunks.forEach(this::remove);
	}

}
