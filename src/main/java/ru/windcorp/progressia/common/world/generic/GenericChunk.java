package ru.windcorp.progressia.common.world.generic;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockFace;

public interface GenericChunk<
	Self extends GenericChunk<Self, B, T, TS>,
	B    extends GenericBlock,
	T    extends GenericTile,
	TS   extends GenericTileStack<TS, T, Self>
> {
	
	public static final int BLOCKS_PER_CHUNK = Coordinates.CHUNK_SIZE;

	Vec3i getPosition();
	
	B getBlock(Vec3i blockInChunk);
	TS getTiles(Vec3i blockInChunk, BlockFace face);
	boolean hasTiles(Vec3i blockInChunk, BlockFace face);
	
	default int getX() {
		return getPosition().x;
	}
	
	default int getY() {
		return getPosition().y;
	}
	
	default int getZ() {
		return getPosition().z;
	}
	
	default boolean containsBiC(Vec3i blockInChunk) {
		return
				blockInChunk.x >= 0 && blockInChunk.x < BLOCKS_PER_CHUNK &&
				blockInChunk.y >= 0 && blockInChunk.y < BLOCKS_PER_CHUNK &&
				blockInChunk.z >= 0 && blockInChunk.z < BLOCKS_PER_CHUNK;
	}
	
	default boolean containsBiW(Vec3i blockInWorld) {
		Vec3i v = Vectors.grab3i();
		
		v = Coordinates.getInWorld(getPosition(), Vectors.ZERO_3i, v);
		v = blockInWorld.sub(v, v);
		
		boolean result = containsBiC(v);
		
		Vectors.release(v);
		return result;
	}
	
	default void forEachBiC(Consumer<? super Vec3i> action) {
		VectorUtil.iterateCuboid(
				0,                0,                0,
				BLOCKS_PER_CHUNK, BLOCKS_PER_CHUNK, BLOCKS_PER_CHUNK,
				action
		);
	}
	
	default void forEachBiW(Consumer<? super Vec3i> action) {
		VectorUtil.iterateCuboid(
				Coordinates.getInWorld(getX(), 0),
				Coordinates.getInWorld(getY(), 0),
				Coordinates.getInWorld(getZ(), 0),
				BLOCKS_PER_CHUNK,
				BLOCKS_PER_CHUNK,
				BLOCKS_PER_CHUNK,
				action
		);
	}
	
	default TS getTilesOrNull(Vec3i blockInChunk, BlockFace face) {
		if (hasTiles(blockInChunk, face)) {
			return getTiles(blockInChunk, face);
		}
		
		return null;
	}
	
}
