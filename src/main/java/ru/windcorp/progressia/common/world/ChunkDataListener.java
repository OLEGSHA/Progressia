package ru.windcorp.progressia.common.world;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;

public interface ChunkDataListener {
	
	/**
	 * Invoked after a block has changed in a chunk.
	 * This is not triggered when a change is caused by chunk loading or unloading.
	 * @param chunk the chunk that has changed
	 * @param blockInChunk the {@linkplain Coordinates#blockInChunk chunk coordinates} of the change
	 * @param previous the previous occupant of {@code blockInChunk}
	 * @param current the current (new) occupant of {@code blockInChunk}
	 */
	 default void onChunkBlockChanged(ChunkData chunk, Vec3i blockInChunk, BlockData previous, BlockData current) {}
	
	/**
	 * Invoked after a tile has been added or removed from a chunk.
	 * This is not triggered when a change is caused by chunk loading or unloading.
	 * @param chunk the chunk that has changed
	 * @param blockInChunk the {@linkplain Coordinates#blockInChunk chunk coordinates} of the change
	 * @param face the face that the changed tile belongs or belonged to 
	 * @param tile the tile that has been added or removed
	 * @param wasAdded {@code true} iff the tile has been added, {@code false} iff the tile has been removed
	 */
	default void onChunkTilesChanged(ChunkData chunk, Vec3i blockInChunk, BlockFace face, TileData tile, boolean wasAdded) {}
	
	/**
	 * Invoked whenever a chunk changes, loads or unloads. If some other method in this
	 * {@code ChunkDataListener} are to be invoked, e.g. is the change was caused by a
	 * block being removed, this method is called last.
	 * @param chunk the chunk that has changed
	 */
	default void onChunkChanged(ChunkData chunk) {}
	
	/**
	 * Invoked whenever a chunk has been loaded.
	 * @param chunk the chunk that has loaded
	 */
	default void onChunkLoaded(ChunkData chunk) {}
	
	/**
	 * Invoked whenever a chunk is about to be unloaded.
	 * @param chunk the chunk that is going to be loaded
	 */
	default void beforeChunkUnloaded(ChunkData chunk) {}

}
