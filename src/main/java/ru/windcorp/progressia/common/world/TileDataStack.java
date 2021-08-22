package ru.windcorp.progressia.common.world;

import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.generic.TileGenericStackWO;
import ru.windcorp.progressia.common.world.tile.TileData;

public interface TileDataStack
	extends TileDataStackRO, TileGenericStackWO<BlockData, TileData, TileDataStack, TileDataReference, ChunkData> {

	@Override
	default boolean isFull() {
		return TileDataStackRO.super.isFull();
	}
	
	/*
	 * Method specialization
	 */
	
	@Override
	TileDataReference getReference(int index);
	
	@Override
	ChunkData getChunk();

}
