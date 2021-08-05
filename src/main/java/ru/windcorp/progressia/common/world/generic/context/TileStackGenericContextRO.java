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

/**
 * A {@link Context} referencing a world with a block location and a block face
 * specified, effectively pointing to a tile stack. The tile stack may or may
 * not actually exist.
 */
//@formatter:off
public interface TileStackGenericContextRO<
	B  extends BlockGeneric,
	T  extends TileGeneric,
	E  extends EntityGeneric
> extends WorldContexts.BlockFace, BlockGenericContextRO<B, T, E> {
//@formatter:on

	/**
	 * Determines whether the specified position has a tile. Block location and
	 * block face are implied by the context.
	 * 
	 * @param layer the layer of the tile
	 * @return {@code true} iff the tile exists
	 */
	default boolean hasTile(int layer) {
		return hasTile(getLocation(), getFace(), layer);
	}

	/**
	 * Determines whether the specified position has a tile with the given tag.
	 * Block location and block face are implied by the context.
	 * 
	 * @param tag the tag of the tile
	 * @return {@code true} iff the tile exists
	 */
	default boolean isTagValid(int tag) {
		return isTagValid(getLocation(), getFace(), tag);
	}

	/**
	 * Retrieves the tile at the specified position. Block location and block
	 * face are implied by the context. This method may return {@code null} in
	 * one of three cases:
	 * <ul>
	 * <li>the location is not loaded,
	 * <li>there is no tile stack on the relevant face, or
	 * <li>{@code layer} is not less than the amount of tiles in the tile stack.
	 * </ul>
	 * 
	 * @return the tile or {@code null} if the position does not contain a tile
	 */
	default T getTile(int layer) {
		return getTile(getLocation(), getFace(), layer);
	}

	/**
	 * Retrieves the tile at the specified position and the tile's tag. Block
	 * location and block face are implied by the context. This
	 * method may return {@code null} in one of three cases:
	 * <ul>
	 * <li>the location is not loaded,
	 * <li>there is no tile stack on the relevant face, or
	 * <li>there is no tile with the specified tag in the tile stack.
	 * </ul>
	 * 
	 * @param tag the tag of the tile
	 * @return the tile or {@code null} if the position does not contain a tile
	 */
	default T getTileByTag(int tag) {
		return getTileByTag(getLocation(), getFace(), tag);
	}

	/**
	 * Counts the amount of tiles in the specified tile stack. Block location
	 * and block face are implied by the context.
	 * <p>
	 * This method returns {@code 0} in case the location is not loaded.
	 * 
	 * @return the count of tiles in the tile stack or {@code -1} if the tile
	 *         stack could not exist
	 */
	default int getTileCount() {
		return getTileCount(getLocation(), getFace());
	}

}
