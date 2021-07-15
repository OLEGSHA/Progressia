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
import ru.windcorp.progressia.common.world.rels.RelFace;

/**
 * A {@link Context} referencing a world with a block location and a block face
 * specified, effectively pointing to a tile stack. The tile stack may or may
 * not actually exist.
 */
//@formatter:off
public interface GenericROBlockFaceContext<
	B  extends GenericBlock,
	T  extends GenericTile,
	TS extends GenericROTileStack     <B, T, TS, TR, C>,
	TR extends GenericROTileReference <B, T, TS, TR, C>,
	C  extends GenericROChunk         <B, T, TS, TR, C>,
	E  extends GenericEntity
> extends GenericROBlockContext<B, T, TS, TR, C, E> {
//@formatter:on

	/**
	 * Returns the face relevant to this context.
	 * 
	 * @return the block face
	 */
	RelFace getFace();

	/**
	 * Gets the tile stack at the relevant position.
	 * 
	 * @return the specified tile stack or {@code null} if the location is not
	 *         loaded or the tile stack does not exist
	 */
	default TS getTilesOrNull() {
		return getTilesOrNull(getLocation(), getFace());
	}

	/**
	 * Determines whether the location relevant to this context has a tile
	 * stack.
	 * 
	 * @return {@code true} iff the tile stack exists
	 */
	default boolean hasTiles() {
		return hasTiles(getLocation(), getFace());
	}

	/**
	 * Determines whether the specified position has a tile; block location and
	 * face are implied by the context.
	 * 
	 * @return {@code true} iff the tile exists
	 */
	default boolean hasTile(int layer) {
		return hasTile(getLocation(), getFace(), layer);
	}

	/**
	 * Gets the tile at the specified position; block location and face are
	 * implied by the context.
	 * 
	 * @return the specified tile or {@code null} if the location is not loaded
	 *         or the tile does not exist
	 */
	default T getTile(int layer) {
		return getTile(getLocation(), getFace(), layer);
	}

}
