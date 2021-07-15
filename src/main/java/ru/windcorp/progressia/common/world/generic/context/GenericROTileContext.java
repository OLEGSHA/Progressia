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
import ru.windcorp.progressia.common.world.generic.GenericBlock;
import ru.windcorp.progressia.common.world.generic.GenericROChunk;
import ru.windcorp.progressia.common.world.generic.GenericEntity;
import ru.windcorp.progressia.common.world.generic.GenericTile;
import ru.windcorp.progressia.common.world.generic.GenericROTileReference;
import ru.windcorp.progressia.common.world.generic.GenericROTileStack;

/**
 * A {@link Context} referencing a world with a block location, a block face and
 * a tile layer specified, effectively pointing to a single tile. The tile may
 * or may not actually exist.
 */
//@formatter:off
public interface GenericROTileContext<
	B  extends GenericBlock,
	T  extends GenericTile,
	TS extends GenericROTileStack     <B, T, TS, TR, C>,
	TR extends GenericROTileReference <B, T, TS, TR, C>,
	C  extends GenericROChunk         <B, T, TS, TR, C>,
	E  extends GenericEntity
> extends GenericROBlockFaceContext<B, T, TS, TR, C, E> {
//@formatter:on

	/**
	 * Returns the tile layer relevant to this context.
	 * 
	 * @return the tile layer
	 */
	int getLayer();

	/**
	 * Determines whether the location relevant to this context has a tile.
	 * 
	 * @return {@code true} iff the tile exists
	 */
	default boolean hasTile() {
		return hasTile(getLocation(), getFace(), getLayer());
	}

	/**
	 * Gets the tile at the relevant position.
	 * 
	 * @return the specified tile or {@code null} if the location is not loaded
	 *         or the tile does not exist
	 */
	default T getTile() {
		return getTile(getLocation(), getFace(), getLayer());
	}

	/**
	 * Gets the tag of the tile at the relevant position.
	 * 
	 * @return the tag of the tile or {@code -1} if the location is not loaded
	 *         or the tile does not exist
	 */
	default int getTag() {
		TS tileStack = getTilesOrNull();
		if (tileStack == null) {
			return -1;
		}

		return tileStack.getTagByIndex(getLayer());
	}

}
