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
 * A {@link Context} referencing a world with a block location, a block face and
 * a tile layer specified, effectively pointing to a single tile. The tile may
 * or may not actually exist.
 */
//@formatter:off
public interface TileGenericContextRO<
	B  extends BlockGeneric,
	T  extends TileGeneric,
	E  extends EntityGeneric
> extends WorldContexts.Tile, BlockFaceGenericContextRO<B, T, E> {
//@formatter:on

	/**
	 * Determines whether the relevant position has a tile.
	 * 
	 * @return {@code true} iff the tile exists
	 */
	default boolean hasTile() {
		return hasTile(getLocation(), getFace(), getLayer());
	}

	/**
	 * Retrieves the tile at the relevant position. This method may return
	 * {@code null} in one of three cases:
	 * <ul>
	 * <li>the location is not loaded,
	 * <li>there is no tile stack on the relevant face, or
	 * <li>{@code layer} is not less than the amount of tiles in the tile stack.
	 * </ul>
	 * 
	 * @return the tile or {@code null} if the position does not contain a tile
	 */
	default T getTile() {
		return getTile(getLocation(), getFace(), getLayer());
	}

}
