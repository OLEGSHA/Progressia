package ru.windcorp.progressia.common.world;

import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.generic.TileGenericReferenceWO;
import ru.windcorp.progressia.common.world.tile.TileData;

public interface TileDataReference extends TileDataReferenceRO,
	TileGenericReferenceWO<BlockData, TileData, TileDataStack, TileDataReference, ChunkData> {

}
