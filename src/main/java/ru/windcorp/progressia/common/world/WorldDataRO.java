package ru.windcorp.progressia.common.world;

import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.WorldGenericRO;
import ru.windcorp.progressia.common.world.tile.TileData;

public interface WorldDataRO
	extends WorldGenericRO<BlockData, TileData, TileDataStackRO, TileDataReferenceRO, ChunkDataRO, EntityData> {

	/**
	 * Returns in-world time since creation. World time is zero before and
	 * during first tick.
	 * <p>
	 * Game logic should assume that this value mostly increases uniformly.
	 * However, it is not guaranteed that in-world time always increments.
	 * 
	 * @return time, in in-game seconds, since the world was created
	 */
	float getTime();

	/**
	 * Gets the {@link GravityModel} used by this world.
	 * 
	 * @return the gravity model
	 */
	GravityModel getGravityModel();

}
