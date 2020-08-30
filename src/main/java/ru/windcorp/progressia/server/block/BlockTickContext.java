package ru.windcorp.progressia.server.block;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.block.BlockData;
import ru.windcorp.progressia.server.world.ChunkTickContext;

public interface BlockTickContext extends ChunkTickContext {
	
	/**
	 * Returns the current world coordinates.
	 * @return the world coordinates of the block being ticked
	 */
	Vec3i getCoords();
	
	/**
	 * Returns the current chunk coordinates.
	 * @return the chunk coordinates of the block being ticked
	 */
	Vec3i getChunkCoords();
	
	default BlockLogic getBlock() {
		return getChunk().getBlock(getChunkCoords());
	}
	
	default BlockData getBlockData() {
		return getChunkData().getBlock(getChunkCoords());
	}

}
