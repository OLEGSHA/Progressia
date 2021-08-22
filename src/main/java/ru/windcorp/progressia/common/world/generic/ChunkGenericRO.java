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

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import glm.Glm;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.AxisRotations;
import ru.windcorp.progressia.common.world.rels.BlockFace;

/**
 * An unmodifiable chunk representation. Per default, it is usually one of
 * {@link ru.windcorp.progressia.common.world.DefaultChunkData ChunkData},
 * {@link ru.windcorp.progressia.client.world.ChunkRender ChunkRender} or
 * {@link ru.windcorp.progressia.server.world.DefaultChunkLogic ChunkLogic}, but this
 * interface may be implemented differently for various reasons.
 * <p>
 * A generic chunk contains {@linkplain BlockGeneric blocks} and
 * {@linkplain TileGenericStackRO tile stacks} and is characterized by its
 * location. It also bears a discrete up direction. Note that no
 * {@linkplain WorldGenericRO world} object is directly accessible through this
 * interface.
 * <p>
 * This interface defines the most common methods for examining a chunk and
 * implements many of them as default methods. It also contains several static
 * methods useful when dealing with chunks. {@code GenericChunk} does not
 * provide a way to modify a chunk; use {@link ChunkGenericWO} methods
 * when applicable.
 * 
 * @param <Self> a reference to itself (required to properly reference a
 *               {@link TileGenericStackRO})
 * @param <B>    block type
 * @param <T>    tile type
 * @param <TS>   tile stack type
 * @author javapony
 */
// @formatter:off
public interface ChunkGenericRO<
	B  extends BlockGeneric,
	T  extends TileGeneric,
	TS extends TileGenericStackRO     <B, T, TS, TR, C>,
	TR extends TileGenericReferenceRO <B, T, TS, TR, C>,
	C  extends ChunkGenericRO         <B, T, TS, TR, C>
> {
// @formatter:on

	/**
	 * The count of blocks in a side of a chunk. This is guaranteed to be a
	 * power of two. This is always equal to {@link Coordinates#CHUNK_SIZE}.
	 */
	public static final int BLOCKS_PER_CHUNK = Coordinates.CHUNK_SIZE;

	/*
	 * Abstract methods
	 */

	/**
	 * Returns the position of this chunk in {@linkplain Coordinates#chunk
	 * coordinates of chunk}. The returned object must not be modified.
	 * 
	 * @return this chunk's position
	 */
	Vec3i getPosition();

	/**
	 * Returns the discrete up direction for this chunk.
	 * 
	 * @return this chunk's discrete up direction
	 */
	AbsFace getUp();

	/**
	 * Retrieves the block at the location specified by its
	 * {@linkplain Coordinates#blockInChunk chunk coordinates}. During chunk
	 * generation it may be {@code null}.
	 * 
	 * @param blockInChunk local coordinates of the block to fetch
	 * @return the block at the requested location or {@code null}.
	 */
	B getBlock(Vec3i blockInChunk);

	TS getTiles(Vec3i blockInChunk, BlockFace face);

	boolean hasTiles(Vec3i blockInChunk, BlockFace face);

	default Vec3i resolve(Vec3i relativeCoords, Vec3i output) {
		return GenericChunks.resolve(relativeCoords, getUp(), output);
	}
	
	default Vec3i relativize(Vec3i absoluteCoords, Vec3i output) {
		return GenericChunks.relativize(absoluteCoords, getUp(), output);
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

	default Vec3i getMinBIW(Vec3i output) {
		if (output == null) {
			output = new Vec3i();
		}

		output.set(getMinX(), getMinY(), getMinZ());

		return output;
	}

	default Vec3i getMaxBIW(Vec3i output) {
		if (output == null) {
			output = new Vec3i();
		}

		output.set(getMaxX(), getMaxY(), getMaxZ());

		return output;
	}

	default Vec3i getMinBIWRel(Vec3i output) {
		if (output == null) {
			output = new Vec3i();
		}

		Vec3i absMin = getMinBIW(Vectors.grab3i());
		Vec3i absMax = getMaxBIW(Vectors.grab3i());

		AxisRotations.relativize(absMin, getUp(), absMin);
		AxisRotations.relativize(absMax, getUp(), absMax);

		Glm.min(absMin, absMax, output);

		Vectors.release(absMax);
		Vectors.release(absMin);

		return output;
	}

	default Vec3i getMaxBIWRel(Vec3i output) {
		if (output == null) {
			output = new Vec3i();
		}

		Vec3i absMin = getMinBIW(Vectors.grab3i());
		Vec3i absMax = getMaxBIW(Vectors.grab3i());

		AxisRotations.relativize(absMin, getUp(), absMin);
		AxisRotations.relativize(absMax, getUp(), absMax);

		Glm.max(absMin, absMax, output);

		Vectors.release(absMax);
		Vectors.release(absMin);

		return output;
	}

	default boolean containsBiW(Vec3i blockInWorld) {
		return GenericChunks.testBiC(blockInWorld, this, GenericChunks::containsBiC);
	}

	default boolean isSurfaceBiW(Vec3i blockInWorld) {
		return GenericChunks.testBiC(blockInWorld, this, GenericChunks::isSurfaceBiC);
	}

	default boolean isEdgeBiW(Vec3i blockInWorld) {
		return GenericChunks.testBiC(blockInWorld, this, GenericChunks::isEdgeBiC);
	}

	default boolean isVertexBiW(Vec3i blockInWorld) {
		return GenericChunks.testBiC(blockInWorld, this, GenericChunks::isVertexBiC);
	}

	default void forEachBiW(Consumer<? super Vec3i> action) {
		int minX = Coordinates.getInWorld(getX(), 0);
		int minY = Coordinates.getInWorld(getY(), 0);
		int minZ = Coordinates.getInWorld(getZ(), 0);
		
		VectorUtil.iterateCuboid(
			minX,
			minY,
			minZ,
			minX + BLOCKS_PER_CHUNK,
			minY + BLOCKS_PER_CHUNK,
			minZ + BLOCKS_PER_CHUNK,
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

	default void forEachTileStack(Consumer<TS> action) {
		GenericChunks.forEachBiC(blockInChunk -> {
			for (AbsFace face : AbsFace.getFaces()) {
				TS stack = getTilesOrNull(blockInChunk, face);
				if (stack == null)
					continue;
				action.accept(stack);
			}
		});
	}

	/**
	 * Iterates over all tiles in this chunk.
	 * 
	 * @param action the action to perform
	 */
	default void forEachTile(BiConsumer<TS, T> action) {
		forEachTileStack(stack -> stack.forEach(tileData -> action.accept(stack, tileData)));
	}

}
