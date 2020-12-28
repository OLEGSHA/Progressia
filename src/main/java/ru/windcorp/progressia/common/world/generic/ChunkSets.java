package ru.windcorp.progressia.common.world.generic;

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
	
	private ChunkSets() {}

}
