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
import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.TLongSet;
import ru.windcorp.progressia.common.util.CoordinatePacker;
import ru.windcorp.progressia.common.util.Vectors;

public class LongBasedChunkSet implements ChunkSet {

	protected final TLongSet impl;

	public LongBasedChunkSet(TLongSet impl) {
		this.impl = impl;
	}

	public LongBasedChunkSet(TLongSet impl, ChunkSet copyFrom) {
		this(impl);
		addAll(copyFrom);
	}

	public LongBasedChunkSet(TLongSet impl, Iterable<? extends Vec3i> copyFrom) {
		this(impl);
		addAll(copyFrom);
	}

	public LongBasedChunkSet(TLongSet impl, GenericWorld<?, ?, ?, ?, ?> copyFrom) {
		this(impl);
		addAllChunks(copyFrom.getChunks());
	}

	private static long getKey(Vec3i v) {
		return CoordinatePacker.pack3IntsIntoLong(v);
	}

	private static Vec3i getVector(long key, Vec3i output) {
		return CoordinatePacker.unpack3IntsFromLong(key, output);
	}

	@Override
	public Iterator<Vec3i> iterator() {
		return new IteratorImpl();
	}

	@Override
	public int size() {
		return impl.size();
	}

	@Override
	public boolean contains(Vec3i pos) {
		return impl.contains(getKey(pos));
	}

	@Override
	public boolean add(Vec3i pos) {
		return impl.add(getKey(pos));
	}

	@Override
	public boolean remove(Vec3i pos) {
		return impl.remove(getKey(pos));
	}

	@Override
	public boolean containsAll(ChunkSet other) {
		if (other instanceof LongBasedChunkSet) {
			return impl.containsAll(((LongBasedChunkSet) other).impl);
		}

		return ChunkSet.super.containsAll((Iterable<? extends Vec3i>) other);
	}

	@Override
	public boolean containsAny(ChunkSet other) {
		return ChunkSet.super.containsAny((Iterable<? extends Vec3i>) other);
	}

	@Override
	public void addAll(ChunkSet other) {
		if (other instanceof LongBasedChunkSet) {
			impl.addAll(((LongBasedChunkSet) other).impl);
			return;
		}

		ChunkSet.super.addAll((Iterable<? extends Vec3i>) other);
	}

	@Override
	public void removeAll(ChunkSet other) {
		if (other instanceof LongBasedChunkSet) {
			impl.removeAll(((LongBasedChunkSet) other).impl);
			return;
		}

		ChunkSet.super.removeAll((Iterable<? extends Vec3i>) other);
	}

	@Override
	public void retainAll(ChunkSet other) {
		if (other instanceof LongBasedChunkSet) {
			impl.retainAll(((LongBasedChunkSet) other).impl);
			return;
		}

		ChunkSet.super.retainAll((Iterable<? extends Vec3i>) other);
	}

	@Override
	public void clear() {
		impl.clear();
	}

	@Override
	public void forEach(Consumer<? super Vec3i> action) {
		Vec3i v = Vectors.grab3i();

		impl.forEach(key -> {
			getVector(key, v);
			action.accept(v);
			return true;
		});

		Vectors.release(v);
	}

	private class IteratorImpl implements Iterator<Vec3i> {

		private final Vec3i vector = new Vec3i();
		private final TLongIterator parent = LongBasedChunkSet.this.impl.iterator();

		@Override
		public boolean hasNext() {
			return parent.hasNext();
		}

		@Override
		public Vec3i next() {
			return getVector(parent.next(), vector);
		}

		@Override
		public void remove() {
			parent.remove();
		}

	}

}
