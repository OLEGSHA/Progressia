package ru.windcorp.progressia.server.world.tile;

import java.util.List;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.world.ChunkLogic;
import ru.windcorp.progressia.server.world.TickAndUpdateUtil;
import ru.windcorp.progressia.server.world.TickContext;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.BlockTickContext;

public interface TileTickContext extends TickContext {
	
	/*
	 * Specifications
	 */
	
	/**
	 * Returns the current world coordinates.
	 * @return the world coordinates of the tile being ticked
	 */
	Vec3i getCurrentBlockInWorld();
	
	/**
	 * Returns the counter world coordinates.
	 * @return the world coordinates of the tile being ticked
	 */
	Vec3i getCounterBlockInWorld();
	
	/**
	 * Returns the current block face.
	 * @return the block face that the tile being ticked occupies
	 */
	BlockFace getCurrentFace();
	
	/**
	 * Returns the current layer.
	 * @return the layer that the tile being ticked occupies in the tile stack
	 */
	int getCurrentLayer();
	
	default BlockFace getCounterFace() {
		return getCurrentFace().getCounter();
	}
	
	/*
	 * Tile-related
	 */
	
	default TileLogic getTile() {
		return getTiles().get(getCurrentLayer());
	}
	
	default TileData getTileData() {
		return getTileDataList().get(getCurrentLayer());
	}
	
	default List<TileLogic> getTiles() {
		return getCurrentChunk().getTiles(
				Coordinates.convertInWorldToInChunk(getCurrentBlockInWorld(), null),
				getCurrentFace()
		);
	}
	
	default List<TileLogic> getTilesOrNull() {
		return getCurrentChunk().getTilesOrNull(
				Coordinates.convertInWorldToInChunk(getCurrentBlockInWorld(), null),
				getCurrentFace()
		);
	}
	
	default List<TileData> getTileDataList() {
		return getCurrentChunkData().getTiles(
				Coordinates.convertInWorldToInChunk(getCurrentBlockInWorld(), null),
				getCurrentFace()
		);
	}
	
	default List<TileData> getTileDataListOrNull() {
		return getCurrentChunkData().getTilesOrNull(
				Coordinates.convertInWorldToInChunk(getCurrentBlockInWorld(), null),
				getCurrentFace()
		);
	}
	
	/*
	 * Current block/chunk
	 */
	
	default ChunkLogic getCurrentChunk() {
		return getWorld().getChunkByBlock(getCurrentBlockInWorld());
	}
	
	default ChunkData getCurrentChunkData() {
		return getWorldData().getChunkByBlock(getCurrentBlockInWorld());
	}
	
	default BlockLogic getCurrentBlock() {
		return getWorld().getBlock(getCurrentBlockInWorld());
	}
	
	default BlockData getCurrentBlockData() {
		return getWorldData().getBlock(getCurrentBlockInWorld());
	}
	
	default BlockTickContext getCurrentBlockContext() {
		return TickAndUpdateUtil.getBlockTickContext(getServer(), getCurrentBlockInWorld());
	}
	
	/*
	 * Counter block/chunk
	 */
	
	default ChunkLogic getCounterChunk() {
		return getWorld().getChunkByBlock(getCounterBlockInWorld());
	}
	
	default ChunkData getCounterChunkData() {
		return getWorldData().getChunkByBlock(getCounterBlockInWorld());
	}
	
	default BlockLogic getCounterBlock() {
		return getWorld().getBlock(getCounterBlockInWorld());
	}
	
	default BlockData getCounterBlockData() {
		return getWorldData().getBlock(getCounterBlockInWorld());
	}
	
	default BlockTickContext getCounterBlockContext() {
		return TickAndUpdateUtil.getBlockTickContext(getServer(), getCounterBlockInWorld());
	}
	
	/*
	 * Convenience methods - changes
	 */
	
	default void removeThisTile() {
		getAccessor().removeTile(getCurrentBlockInWorld(), getCurrentFace(), getTileData());
	}

}
