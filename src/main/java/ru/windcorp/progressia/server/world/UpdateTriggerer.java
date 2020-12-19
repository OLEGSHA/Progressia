package ru.windcorp.progressia.server.world;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.ChunkDataListener;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.Server;

public class UpdateTriggerer implements ChunkDataListener {

	private final Server server;

	public UpdateTriggerer(Server server) {
		this.server = server;
	}

	@Override
	public void onChunkBlockChanged(
			ChunkData chunk, Vec3i blockInChunk, BlockData previous, BlockData current
	) {
		server.getWorldAccessor().triggerUpdates(Coordinates.getInWorld(chunk.getPosition(), blockInChunk, null));
	}
	
	@Override
	public void onChunkTilesChanged(
			ChunkData chunk, Vec3i blockInChunk, BlockFace face, TileData tile,
			boolean wasAdded
	) {
		server.getWorldAccessor().triggerUpdates(Coordinates.getInWorld(chunk.getPosition(), blockInChunk, null), face);
	}

}
