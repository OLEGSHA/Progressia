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

import ru.windcorp.progressia.common.world.context.Context;
import ru.windcorp.progressia.common.world.generic.*;
import ru.windcorp.progressia.common.world.rels.BlockFace;

/**
 * A {@link Context} referencing a world with a block location specified. The
 * location may or may not be loaded.
 */
//@formatter:off
public interface BlockGenericContextRO<
	B  extends BlockGeneric,
	T  extends TileGeneric,
	E  extends EntityGeneric
> extends WorldContexts.Block, WorldGenericContextRO<B, T, E> {
//@formatter:on

	/**
	 * Determines whether the location relevant to this context is currently
	 * loaded.
	 * 
	 * @return {@code true} iff the location is loaded
	 */
	default boolean isLoaded() {
		return isLocationLoaded(getLocation());
	}

	/**
	 * Retrieves the block at the relevant location. This method may return
	 * {@code null} in one of two cases:
	 * <ul>
	 * <li>the location that the block would occupy is not loaded, or
	 * <li>the corresponding chunk's terrain has not yet generated.
	 * </ul>
	 * 
	 * @return the block or {@code null} if the location is not loaded
	 */
	default B getBlock() {
		return getBlock(getLocation());
	}

	/**
	 * Determines whether the specified position has a tile. Block location is
	 * implied by the context.
	 * 
	 * @param face  the face of the block that the tile occupies
	 * @param layer the layer of the tile
	 * @return {@code true} iff the tile exists
	 */
	default boolean hasTile(BlockFace face, int layer) {
		return hasTile(getLocation(), face, layer);
	}

	/**
	 * Determines whether the specified position has a tile with the given tag.
	 * Block location is implied by context.
	 * 
	 * @param face the face of the block that the tile occupies
	 * @param tag  the tag of the tile
	 * @return {@code true} iff the tile exists
	 */
	default boolean isTagValid(BlockFace face, int tag) {
		return isTagValid(getLocation(), face, tag);
	}

	/**
	 * Retrieves the tile at the specified position. Block location is implied
	 * by context. This method may return {@code null} in one of three cases:
	 * <ul>
	 * <li>the location is not loaded,
	 * <li>there is no tile stack on the specified face, or
	 * <li>{@code layer} is not less than the amount of tiles in the tile stack.
	 * </ul>
	 * 
	 * @param face  the face of the block that the tile occupies
	 * @param layer the layer of the tile stack that the tile occupies
	 * @return the tile or {@code null} if the position does not contain a tile
	 */
	default T getTile(BlockFace face, int layer) {
		return getTile(getLocation(), face, layer);
	}

	/**
	 * Retrieves the tile at the specified position and the tile's tag. Block
	 * location is implied by the context. This
	 * method may return {@code null} in one of three cases:
	 * <ul>
	 * <li>the location is not loaded,
	 * <li>there is no tile stack on the specified face, or
	 * <li>there is no tile with the specified tag in the tile stack.
	 * </ul>
	 * 
	 * @param face the face of the block that the tile occupies
	 * @param tag  the tag of the tile
	 * @return the tile or {@code null} if the position does not contain a tile
	 */
	default T getTileByTag(BlockFace face, int tag) {
		return getTileByTag(getLocation(), face, tag);
	}

	/**
	 * Counts the amount of tiles in the specified tile stack. Block location is
	 * implied by the context
	 * <p>
	 * This method returns {@code 0} in case the location is not loaded.
	 * 
	 * @param face the face of the block that the tile stack occupies
	 * @return the count of tiles in the tile stack or {@code -1} if the tile
	 *         stack could not exist
	 */
	default int getTileCount(BlockFace face) {
		return getTileCount(face);
	}

}
