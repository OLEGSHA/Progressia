package ru.windcorp.progressia.common.world;

import java.util.Collection;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.WorldGenericWO;
import ru.windcorp.progressia.common.world.rels.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;

public interface WorldData
	extends WorldDataRO, WorldGenericWO<BlockData, TileData, TileDataStack, TileDataReference, ChunkData, EntityData> {

	@Override
	default TileDataStack getTiles(Vec3i blockInWorld, BlockFace face) {
		return (TileDataStack) WorldDataRO.super.getTiles(blockInWorld, face);
	}

	/**
	 * Increases in-game time of this world by {@code change}. Total time is
	 * decreased when {@code change} is negative.
	 * 
	 * @param change the amount of time to add to current world time. May be
	 *               negative.
	 * @see #getTime()
	 */
	void advanceTime(float change);
	
	/*
	 * Method specialization
	 */
	
	@Override
	ChunkData getChunk(Vec3i pos);
	
	@Override
	Collection<? extends ChunkData> getChunks();
	
	// TODO: rename WGRO.forEachChunk -> forEachChunkRO and define WGWO.forEachChunk
	
	@Override
	default ChunkData getChunkByBlock(Vec3i blockInWorld) {
		return (ChunkData) WorldDataRO.super.getChunkByBlock(blockInWorld);
	}
	
	@Override
	default TileDataStack getTilesOrNull(Vec3i blockInWorld, BlockFace face) {
		return (TileDataStack) WorldDataRO.super.getTilesOrNull(blockInWorld, face);
	}

}
