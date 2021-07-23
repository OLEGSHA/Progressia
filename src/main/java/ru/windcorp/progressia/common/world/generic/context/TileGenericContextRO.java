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
	TS extends TileGenericStackRO     <B, T, TS, TR, C>,
	TR extends TileGenericReferenceRO <B, T, TS, TR, C>,
	C  extends ChunkGenericRO         <B, T, TS, TR, C>,
	E  extends EntityGeneric
> extends WorldContexts.Tile, BlockFaceGenericContextRO<B, T, TS, TR, C, E> {
//@formatter:on

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

	@Override
	default int getTag() {
		TS tileStack = getTilesOrNull();
		if (tileStack == null) {
			return -1;
		}

		return tileStack.getTagByIndex(getLayer());
	}

	/**
	 * Gets the {@link TileGenericReferenceRO TileReference} to the relevant
	 * tile.
	 * 
	 * @return the reference to the tile relevant to this context or
	 *         {@code null} if the location is not loaded or the tile does not
	 *         exist
	 *         
	 * @see TileGenericStackRO#getReference(int)
	 */
	default TR getTileReference() {
		TS tileStack = getTilesOrNull();
		if (tileStack == null) {
			return null;
		}

		return tileStack.getReference(getLayer());
	}

}
