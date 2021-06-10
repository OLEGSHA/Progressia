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

import java.util.Collection;
import java.util.function.Consumer;

import glm.Glm;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockFace;

public interface GenericWorld<B extends GenericBlock, T extends GenericTile, TS extends GenericTileStack<TS, T, C>, C extends GenericChunk<C, B, T, TS>, E extends GenericEntity> {

	Collection<C> getChunks();

	C getChunk(Vec3i pos);

	Collection<E> getEntities();

	/*
	 * Chunks
	 */

	default C getChunkByBlock(Vec3i blockInWorld) {
		Vec3i chunkCoords = Vectors.grab3i();
		chunkCoords = Coordinates.convertInWorldToChunk(blockInWorld, chunkCoords);
		C result = getChunk(chunkCoords);
		Vectors.release(chunkCoords);
		return result;
	}

	default B getBlock(Vec3i blockInWorld) {
		Vec3i v = Vectors.grab3i();
		B result;

		C chunk = getChunk(Coordinates.convertInWorldToChunk(blockInWorld, v));
		if (chunk == null) {
			result = null;
		} else {
			result = chunk.getBlock(Coordinates.convertInWorldToInChunk(blockInWorld, v));
		}

		Vectors.release(v);
		return result;
	}

	default TS getTiles(Vec3i blockInWorld, BlockFace face) {
		Vec3i v = Vectors.grab3i();
		TS result;

		C chunk = getChunk(Coordinates.convertInWorldToChunk(blockInWorld, v));
		if (chunk == null) {
			result = null;
		} else {
			result = chunk.getTiles(Coordinates.convertInWorldToInChunk(blockInWorld, v), face);
		}

		Vectors.release(v);
		return result;
	}

	default TS getTilesOrNull(Vec3i blockInWorld, BlockFace face) {
		Vec3i v = Vectors.grab3i();
		TS result;

		C chunk = getChunk(Coordinates.convertInWorldToChunk(blockInWorld, v));
		if (chunk == null) {
			result = null;
		} else {
			result = chunk.getTilesOrNull(Coordinates.convertInWorldToInChunk(blockInWorld, v), face);
		}

		Vectors.release(v);
		return result;
	}

	default boolean hasTiles(Vec3i blockInWorld, BlockFace face) {
		Vec3i v = Vectors.grab3i();
		boolean result;

		C chunk = getChunk(Coordinates.convertInWorldToChunk(blockInWorld, v));
		if (chunk == null) {
			result = false;
		} else {
			result = chunk.hasTiles(Coordinates.convertInWorldToInChunk(blockInWorld, v), face);
		}

		Vectors.release(v);
		return result;
	}

	default T getTile(Vec3i blockInWorld, BlockFace face, int layer) {
		TS stack = getTilesOrNull(blockInWorld, face);
		if (stack == null || stack.size() <= layer)
			return null;
		return stack.get(layer);
	}

	default boolean isChunkLoaded(Vec3i chunkPos) {
		return getChunk(chunkPos) != null;
	}

	default boolean isBlockLoaded(Vec3i blockInWorld) {
		return getChunkByBlock(blockInWorld) != null;
	}

	default void forEachChunk(Consumer<? super C> action) {
		getChunks().forEach(action);
	}

	/*
	 * Entities
	 */

	default void forEachEntity(Consumer<? super E> action) {
		getEntities().forEach(action);
	}

	default void forEachEntityIn(Vec3i min, Vec3i max, Consumer<? super E> action) {
		forEachEntity(e -> {
			Vec3 pos = e.getPosition();
			if (pos.x < min.x || pos.y < min.y || pos.z < min.z || pos.x > max.x || pos.y > max.y || pos.z > max.z) {
				action.accept(e);
			}
		});
	}

	default void forEachEntityIn(Vec3 min, Vec3 max, Consumer<? super E> action) {
		forEachEntity(e -> {
			Vec3 pos = e.getPosition();
			if (pos.x < min.x || pos.y < min.y || pos.z < min.z || pos.x > max.x || pos.y > max.y || pos.z > max.z) {
				action.accept(e);
			}
		});
	}

	default void forEachEntityInChunk(Vec3i pos, Consumer<? super E> action) {
		Vec3i v = Vectors.grab3i();

		forEachEntity(e -> {
			e.getChunkCoords(v);
			if (Glm.equals(v, pos)) {
				action.accept(e);
			}
		});

		Vectors.release(v);
	}

	default void forEachEntityInChunk(C chunk, Consumer<? super E> action) {
		Vec3i v = Vectors.grab3i();

		forEachEntity(e -> {
			e.getChunkCoords(v);
			if (Glm.equals(v, chunk.getPosition())) {
				action.accept(e);
			}
		});

		Vectors.release(v);
	}

	default void forEachEntityWithId(String id, Consumer<? super E> action) {
		forEachEntity(e -> {
			if (id.equals(e.getId())) {
				action.accept(e);
			}
		});
	}

}
