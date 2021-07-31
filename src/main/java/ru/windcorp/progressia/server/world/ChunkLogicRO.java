package ru.windcorp.progressia.server.world;

import ru.windcorp.progressia.common.world.ChunkDataRO;
import ru.windcorp.progressia.common.world.generic.ChunkGenericRO;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.tile.TileLogic;

public interface ChunkLogicRO
	extends ChunkGenericRO<BlockLogic, TileLogic, TileLogicStackRO, TileLogicReferenceRO, ChunkLogicRO> {
	
	ChunkDataRO getData();

	boolean isReady();

}
