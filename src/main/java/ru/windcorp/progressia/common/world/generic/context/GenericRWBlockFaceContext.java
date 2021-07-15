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
 * A writable {@link Context} referencing a world with a block location and a
 * block face specified, effectively pointing to a tile stack. This context
 * provides methods for affecting the world. The application of requested
 * changes may or may not be immediate, see {@link #isImmediate()}. The tile
 * stack may or may not actually exist.
 */
//@formatter:off
public interface GenericRWBlockFaceContext<
	B  extends GenericBlock,
	T  extends GenericTile,
	TS extends GenericRWTileStack <B, T, TS, TR, C>,
	TR extends GenericROTileReference     <B, T, TS, TR, C>,
	C  extends GenericRWChunk     <B, T, TS, TR, C>,
	E  extends GenericEntity
> extends GenericRWBlockContext<B, T, TS, TR, C, E>, GenericROBlockFaceContext<B, T, TS, TR, C, E> {
//@formatter:on

	/**
	 * Requests that a tile is added to the top of the tile stack at the given
	 * location. The object provided may be stored until the change is applied.
	 * If the tile could not be added at the time of application this method
	 * fails silently. The location and the face of the block are implied by the
	 * context.
	 * 
	 * @param tile the tile to add
	 */
	default void addTile(T tile) {
		addTile(getLocation(), getFace(), tile);
	}

	/**
	 * Requests that a tile identified by its tag is removed from the specified
	 * tile stack. If the tile could not be found at the time of application
	 * this method fails silently. The location and the face of the block are
	 * implied by the context.
	 * 
	 * @param tag the tag of the tile to remove
	 */
	default void removeTile(int tag) {
		removeTile(getLocation(), getFace(), tag);
	}

	/**
	 * Requests that the referenced tile is removed from the specified tile
	 * stack. If the tile could not be found at the time of application this
	 * method fails silently. The location and the face of the block are implied
	 * by the context.
	 * 
	 * @param tileReference a reference to the tile
	 */
	default void removeTile(TR tileReference) {
		removeTile(getLocation(), getFace(), tileReference.getTag());
	}

}
