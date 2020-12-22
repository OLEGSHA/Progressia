package ru.windcorp.progressia.client.world.tile;

import ru.windcorp.progressia.client.world.ChunkRender;
import ru.windcorp.progressia.common.world.generic.GenericTileStack;
import ru.windcorp.progressia.common.world.tile.TileDataStack;

public abstract class TileRenderStack
extends GenericTileStack<
	TileRenderStack,
	TileRender,
	ChunkRender
> {
	
	public abstract TileDataStack getData();
	
}
