package ru.windcorp.progressia.server.world.tile;

import ru.windcorp.progressia.common.world.generic.GenericTileStack;
import ru.windcorp.progressia.common.world.tile.TileDataStack;
import ru.windcorp.progressia.server.world.ChunkLogic;

public abstract class TileLogicStack
extends GenericTileStack<
	TileLogicStack,
	TileLogic,
	ChunkLogic
> {
	
	public abstract TileDataStack getData();

}
