package ru.windcorp.progressia.common.world;

import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.generic.ChunkGenericRO;
import ru.windcorp.progressia.common.world.tile.TileData;

public interface ChunkDataRO
	extends ChunkGenericRO<BlockData, TileData, TileDataStackRO, TileDataReferenceRO, ChunkDataRO> {

	// currently empty

}
