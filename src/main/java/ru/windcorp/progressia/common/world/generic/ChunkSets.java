package ru.windcorp.progressia.common.world.generic;

import java.util.Iterator;
import java.util.NoSuchElementException;

import glm.vec._3.i.Vec3i;
import gnu.trove.impl.sync.TSynchronizedLongSet;
import gnu.trove.set.hash.TLongHashSet;

public class ChunkSets {
	
	public static ChunkSet newHashSet() {
		return new LongBasedChunkSet(new TLongHashSet());
	}
	
	public static ChunkSet newSyncHashSet(Object mutex) {
		return new LongBasedChunkSet(new TSynchronizedLongSet(new TLongHashSet(), mutex));
	}
	
	public static ChunkSet newSyncHashSet() {
		return new LongBasedChunkSet(new TSynchronizedLongSet(new TLongHashSet()));
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
	
	public static ChunkSet empty() {
		return EMPTY_SET;
	}
	
	private ChunkSets() {}

}
