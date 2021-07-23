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
 * A writable {@link Context} referencing a world with a block location
 * specified. This context provides methods for affecting the world. The
 * application of requested changes may or may not be immediate, see
 * {@link #isImmediate()}. The location may or may not be loaded.
 */
//@formatter:off
public interface BlockGenericContextWO<
	B  extends BlockGeneric,
	T  extends TileGeneric,
	TS extends TileGenericStackWO     <B, T, TS, TR, C>,
	TR extends TileGenericReferenceWO <B, T, TS, TR, C>,
	C  extends ChunkGenericWO         <B, T, TS, TR, C>,
	E  extends EntityGeneric
> extends WorldContexts.Block, WorldGenericContextWO<B, T, TS, TR, C, E> {
//@formatter:on

	/**
	 * Requests that a block is changed. The object provided may be stored until
	 * the change is applied. The location of the block is implied by the
	 * context.
	 * 
	 * @param block the new block
	 * @see #isImmediate()
	 */
	default void setBlock(B block) {
		setBlock(getLocation(), block);
	}

	/**
	 * Requests that a tile is added to the top of the tile stack at the given
	 * location. The object provided may be stored until the change is applied.
	 * If the tile could not be added at the time of application this method
	 * fails silently. The location of the block is implied by the context.
	 * 
	 * @param face the face of the block to add the tile to
	 * @param tile the tile to add
	 */
	default void addTile(BlockFace face, T tile) {
		addTile(getLocation(), face, tile);
	}

	/**
	 * Requests that a tile identified by its tag is removed from the specified
	 * tile stack. If the tile could not be found at the time of application
	 * this method fails silently. The location of the block is implied by the
	 * context.
	 * 
	 * @param face the of the block to remove the tile from
	 * @param tag  the tag of the tile to remove
	 */
	default void removeTile(BlockFace face, int tag) {
		removeTile(getLocation(), face, tag);
	}

}
