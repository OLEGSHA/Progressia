package ru.windcorp.progressia.server.world.tile;

import ru.windcorp.progressia.common.world.tile.GenericTileStack;
import ru.windcorp.progressia.common.world.tile.TileDataStack;
import ru.windcorp.progressia.server.world.ChunkLogic;

public abstract class TileLogicStack extends GenericTileStack<TileLogic, ChunkLogic> {
	
	// TODO add @Deprecated or smth similar to all modification methods
	
	public abstract TileDataStack getData();

}
