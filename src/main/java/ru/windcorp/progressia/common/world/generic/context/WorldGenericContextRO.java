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
package ru.windcorp.progressia.common.world.generic.context;

import java.util.Collection;
import java.util.function.Consumer;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.context.Context;
import ru.windcorp.progressia.common.world.generic.*;
import ru.windcorp.progressia.common.world.rels.BlockFace;

/**
 * A {@link Context} with a world instance.
 * <p>
 * This interfaces defines the entirety of world query methods supported by the
 * default contexts.
 */
// @formatter:off
public interface WorldGenericContextRO<
	B  extends BlockGeneric,
	T  extends TileGeneric,
	E  extends EntityGeneric
> extends WorldContexts.World {
// @formatter:on

	/**
	 * Retrieves the block at the specified location. This method may return
	 * {@code null} in one of two cases:
	 * <ul>
	 * <li>the location that the block would occupy is not loaded, or
	 * <li>the corresponding chunk's terrain has not yet generated.
	 * </ul>
	 * 
	 * @param location the location to query
	 * @return the block or {@code null} if the location is not loaded
	 */
	B getBlock(Vec3i location);

	/**
	 * Determines whether the specified location is loaded.
	 * 
	 * @param location the location to query
	 * @return {@code true} iff the location is loaded
	 */
	boolean isLocationLoaded(Vec3i location);

	/**
	 * Retrieves the tile at the specified position. This method may return
	 * {@code null} in one of three cases:
	 * <ul>
	 * <li>the location is not loaded,
	 * <li>there is no tile stack on the specified face, or
	 * <li>{@code layer} is not less than the amount of tiles in the tile stack.
	 * </ul>
	 * 
	 * @param location location of the host block
	 * @param face     the face of the block that the tile occupies
	 * @param layer    the layer of the tile stack that the tile occupies
	 * @return the tile or {@code null} if the position does not contain a tile
	 */
	T getTile(Vec3i location, BlockFace face, int layer);

	/**
	 * Retrieves the tile at the specified position and the tile's tag. This
	 * method may return {@code null} in one of three cases:
	 * <ul>
	 * <li>the location is not loaded,
	 * <li>there is no tile stack on the specified face, or
	 * <li>there is no tile with the specified tag in the tile stack.
	 * </ul>
	 * 
	 * @param location location of the host block
	 * @param face     the face of the block that the tile occupies
	 * @param tag      the tag of the tile
	 * @return the tile or {@code null} if the position does not contain a tile
	 */
	T getTileByTag(Vec3i location, BlockFace face, int tag);

	/**
	 * Determines whether the specified position has a tile.
	 * 
	 * @param location location of the host block
	 * @param face     the face of the block that the tile occupies
	 * @param layer    the layer of the tile
	 * @return {@code true} iff the tile exists
	 */
	boolean hasTile(Vec3i location, BlockFace face, int layer);

	/**
	 * Determines whether the specified position has a tile with the given tag.
	 * 
	 * @param location location of the host block
	 * @param face     the face of the block that the tile occupies
	 * @param tag      the tag of the tile
	 * @return {@code true} iff the tile exists
	 */
	boolean isTagValid(Vec3i location, BlockFace face, int tag);

	/**
	 * Counts the amount of tiles in the specified tile stack.
	 * <p>
	 * This method returns {@code 0} in case the location is not loaded.
	 * 
	 * @param location location of the host block
	 * @param face     the face of the block that the tile stack occupies
	 * @return the count of tiles in the tile stack or {@code -1} if the tile
	 *         stack could not exist
	 */
	int getTileCount(Vec3i location, BlockFace face);

	/**
	 * Retrieves a listing of all entities. {@link #forEachEntity(Consumer)}
	 * should be used to iterate the collection. The collection is not
	 * modifiable.
	 * 
	 * @return all loaded entities
	 */
	Collection<E> getEntities();

	/**
	 * Retrieves the entity with the specified entity ID.
	 * 
	 * @param entityId the entity ID to look up
	 * @return the entity found or {@code null}
	 */
	E getEntity(long entityId);

	/*
	 * Convenience methods
	 */

	/**
	 * Iterates all entities safely
	 */
	default void forEachEntity(Consumer<? super E> action) {
		getEntities().forEach(action);
	}

	/**
	 * Iterates all entities in cuboid safely
	 */
	default void forEachEntityIn(Vec3i min, Vec3i max, Consumer<? super E> action) {
		forEachEntity(e -> {
			Vec3 pos = e.getPosition();
			if (pos.x < min.x || pos.y < min.y || pos.z < min.z || pos.x > max.x || pos.y > max.y || pos.z > max.z) {
				action.accept(e);
			}
		});
	}

	/**
	 * Iterates all entities in cuboid safely
	 */
	default void forEachEntityIn(Vec3 min, Vec3 max, Consumer<? super E> action) {
		forEachEntity(e -> {
			Vec3 pos = e.getPosition();
			if (pos.x < min.x || pos.y < min.y || pos.z < min.z || pos.x > max.x || pos.y > max.y || pos.z > max.z) {
				action.accept(e);
			}
		});
	}

	/**
	 * Iterates all entities with ID safely
	 */
	default void forEachEntityWithId(String id, Consumer<? super E> action) {
		forEachEntity(e -> {
			if (id.equals(e.getId())) {
				action.accept(e);
			}
		});
	}

}
