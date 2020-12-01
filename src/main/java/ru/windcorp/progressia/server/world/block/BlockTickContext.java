package ru.windcorp.progressia.server.world.block;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.server.world.ChunkTickContext;

public interface BlockTickContext extends ChunkTickContext {
	
	/**
	 * Returns the current world coordinates.
	 * @return the world coordinates of the block being ticked
	 */
	Vec3i getBlockInWorld();
	
	default BlockLogic getBlock() {
		return getWorld().getBlock(getBlockInWorld());
	}
	
	default BlockData getBlockData() {
		return getWorldData().getBlock(getBlockInWorld());
	}

	/*
	 * Convenience methods - changes
	 */
	
	default void setThisBlock(BlockData block) {
		getAccessor().setBlock(getBlockInWorld(), block);
	}
	
	default void setThisBlock(String id) {
		getAccessor().setBlock(getBlockInWorld(), id);
	}

}
