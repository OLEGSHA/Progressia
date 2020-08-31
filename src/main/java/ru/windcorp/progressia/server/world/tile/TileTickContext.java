package ru.windcorp.progressia.server.world.tile;

import java.util.List;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.world.ChunkTickContext;
import ru.windcorp.progressia.server.world.block.BlockLogic;

public interface TileTickContext extends ChunkTickContext {
	
	/**
	 * Returns the current world coordinates.
	 * @return the world coordinates of the tile being ticked
	 */
	Vec3i getCoords();
	
	/**
	 * Returns the current chunk coordinates.
	 * @return the chunk coordinates of the tile being ticked
	 */
	Vec3i getChunkCoords();
	
	/**
	 * Returns the current block face. This face is always
	 * {@linkplain BlockFace#isPrimary() primary}.
	 * @return the block face that the tile being ticked occupies
	 */
	BlockFace getFace();
	
	/**
	 * Returns the current layer.
	 * @return the layer that the tile being ticked occupies in the tile stack
	 */
	int getLayer();
	
	default TileLogic getTile() {
		return getTiles().get(getLayer());
	}
	
	default TileData getTileData() {
		return getTileDataList().get(getLayer());
	}
	
	default List<TileLogic> getTiles() {
		return getChunk().getTiles(getChunkCoords(), getFace());
	}
	
	default List<TileLogic> getTilesOrNull() {
		return getChunk().getTilesOrNull(getChunkCoords(), getFace());
	}
	
	default List<TileData> getTileDataList() {
		return getChunkData().getTiles(getChunkCoords(), getFace());
	}
	
	default List<TileData> getTileDataListOrNull() {
		return getChunkData().getTilesOrNull(getChunkCoords(), getFace());
	}
	
	default BlockLogic getBlock() {
		return getChunk().getBlock(getChunkCoords());
	}
	
	default BlockData getBlockData() {
		return getChunkData().getBlock(getChunkCoords());
	}

}
