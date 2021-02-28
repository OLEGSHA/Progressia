/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
 
package ru.windcorp.progressia.common.world.generic;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.AxisRotations;
import ru.windcorp.progressia.common.world.rels.BlockFace;

public interface GenericChunk<Self extends GenericChunk<Self, B, T, TS>, B extends GenericBlock, T extends GenericTile, TS extends GenericTileStack<TS, T, Self>> {

	public static final int BLOCKS_PER_CHUNK = Coordinates.CHUNK_SIZE;

	Vec3i getPosition();
	
	AbsFace getUp();

	B getBlock(Vec3i blockInChunk);

	TS getTiles(Vec3i blockInChunk, BlockFace face);

	boolean hasTiles(Vec3i blockInChunk, BlockFace face);
	
	default Vec3i resolve(Vec3i relativeBlockInChunk, Vec3i output) {
		if (output == null) {
			output = new Vec3i();
		}
		
		final int offset = BLOCKS_PER_CHUNK - 1;
		
		output.set(relativeBlockInChunk.x, relativeBlockInChunk.y, relativeBlockInChunk.z);
		output.mul(2).sub(offset);
		
		AxisRotations.resolve(output, getUp(), output);
		
		output.add(offset).div(2);
		
		return output;
	}
	
	default B getBlockRel(Vec3i relativeBlockInChunk) {
		Vec3i absoluteBlockInChunk = Vectors.grab3i();
		resolve(relativeBlockInChunk, absoluteBlockInChunk);
		B result = getBlock(absoluteBlockInChunk);
		Vectors.release(absoluteBlockInChunk);
		return result;
	}
	
	default TS getTilesRel(Vec3i relativeBlockInChunk, BlockFace face) {
		Vec3i absoluteBlockInChunk = Vectors.grab3i();
		resolve(relativeBlockInChunk, absoluteBlockInChunk);
		TS result = getTiles(absoluteBlockInChunk, face);
		Vectors.release(absoluteBlockInChunk);
		return result;
	}
	
	default boolean hasTilesRel(Vec3i relativeBlockInChunk, BlockFace face) {
		Vec3i absoluteBlockInChunk = Vectors.grab3i();
		resolve(relativeBlockInChunk, absoluteBlockInChunk);
		boolean result = hasTiles(absoluteBlockInChunk, face);
		Vectors.release(absoluteBlockInChunk);
		return result;
	}
	
	default int getX() {
		return getPosition().x;
	}

	default int getMinX() {
		return Coordinates.getInWorld(getX(), 0);
	}

	default int getMaxX() {
		return Coordinates.getInWorld(getX(), BLOCKS_PER_CHUNK - 1);
	}

	default int getY() {
		return getPosition().y;
	}

	default int getMinY() {
		return Coordinates.getInWorld(getY(), 0);
	}

	default int getMaxY() {
		return Coordinates.getInWorld(getY(), BLOCKS_PER_CHUNK - 1);
	}

	default int getZ() {
		return getPosition().z;
	}

	default int getMinZ() {
		return Coordinates.getInWorld(getZ(), 0);
	}

	default int getMaxZ() {
		return Coordinates.getInWorld(getZ(), BLOCKS_PER_CHUNK - 1);
	}

	default boolean containsBiC(Vec3i blockInChunk) {
		return blockInChunk.x >= 0 && blockInChunk.x < BLOCKS_PER_CHUNK &&
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
	
	default boolean isSurfaceBiC(Vec3i blockInChunk) {
		int hits = 0;
		
		if (Coordinates.isOnChunkBorder(blockInChunk.x)) hits++;
		if (Coordinates.isOnChunkBorder(blockInChunk.y)) hits++;
		if (Coordinates.isOnChunkBorder(blockInChunk.z)) hits++;
		
		return hits >= 1;
	}
	
	default boolean isSurfaceBiW(Vec3i blockInWorld) {
		Vec3i v = Vectors.grab3i();

		v = Coordinates.getInWorld(getPosition(), Vectors.ZERO_3i, v);
		v = blockInWorld.sub(v, v);

		boolean result = isSurfaceBiC(v);

		Vectors.release(v);
		return result;
	}
	
	default boolean isEdgeBiC(Vec3i blockInChunk) {
		int hits = 0;
		
		if (Coordinates.isOnChunkBorder(blockInChunk.x)) hits++;
		if (Coordinates.isOnChunkBorder(blockInChunk.y)) hits++;
		if (Coordinates.isOnChunkBorder(blockInChunk.z)) hits++;
		
		return hits >= 2;
	}
	
	default boolean isEdgeBiW(Vec3i blockInWorld) {
		Vec3i v = Vectors.grab3i();

		v = Coordinates.getInWorld(getPosition(), Vectors.ZERO_3i, v);
		v = blockInWorld.sub(v, v);

		boolean result = isEdgeBiC(v);

		Vectors.release(v);
		return result;
	}
	
	default boolean isVertexBiC(Vec3i blockInChunk) {
		int hits = 0;
		
		if (Coordinates.isOnChunkBorder(blockInChunk.x)) hits++;
		if (Coordinates.isOnChunkBorder(blockInChunk.y)) hits++;
		if (Coordinates.isOnChunkBorder(blockInChunk.z)) hits++;
		
		return hits == 3;
	}
	
	default boolean isVertexBiW(Vec3i blockInWorld) {
		Vec3i v = Vectors.grab3i();

		v = Coordinates.getInWorld(getPosition(), Vectors.ZERO_3i, v);
		v = blockInWorld.sub(v, v);

		boolean result = isVertexBiC(v);

		Vectors.release(v);
		return result;
	}

	default void forEachBiC(Consumer<? super Vec3i> action) {
		VectorUtil.iterateCuboid(
			0,
			0,
			0,
			BLOCKS_PER_CHUNK,
			BLOCKS_PER_CHUNK,
			BLOCKS_PER_CHUNK,
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
	
	default TS getTilesOrNullRel(Vec3i relativeBlockInChunk, BlockFace face) {
		Vec3i absoluteBlockInChunk = Vectors.grab3i();
		resolve(relativeBlockInChunk, absoluteBlockInChunk);
		
		TS result;
		
		if (hasTiles(absoluteBlockInChunk, face)) {
			result = getTiles(absoluteBlockInChunk, face);
		} else {
			result = null;
		}
		
		Vectors.release(absoluteBlockInChunk);
		
		return result;
	}

}
