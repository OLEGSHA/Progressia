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

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.context.Context;
import ru.windcorp.progressia.common.world.generic.GenericBlock;
import ru.windcorp.progressia.common.world.generic.GenericROChunk;
import ru.windcorp.progressia.common.world.generic.GenericEntity;
import ru.windcorp.progressia.common.world.generic.GenericTile;
import ru.windcorp.progressia.common.world.generic.GenericROTileReference;
import ru.windcorp.progressia.common.world.generic.GenericROTileStack;
import ru.windcorp.progressia.common.world.rels.BlockFace;

/**
 * A {@link Context} referencing a world with a block location specified. The
 * location may or may not be loaded.
 */
//@formatter:off
public interface GenericROBlockContext<
	B  extends GenericBlock,
	T  extends GenericTile,
	TS extends GenericROTileStack     <B, T, TS, TR, C>,
	TR extends GenericROTileReference <B, T, TS, TR, C>,
	C  extends GenericROChunk         <B, T, TS, TR, C>,
	E  extends GenericEntity
> extends GenericROWorldContext<B, T, TS, TR, C, E> {
//@formatter:on

	/**
	 * Returns the location of the block.
	 * <p>
	 * The coordinate system in use is not specified, but it is consistent
	 * across all methods of this context.
	 * <p>
	 * The object returned by this method must not be modified. It is only valid
	 * while the context is {@linkplain valid}.
	 * 
	 * @return a vector describing the block's position
	 */
	Vec3i getLocation();

	/**
	 * Determines whether the location relevant to this context is currently
	 * loaded.
	 * 
	 * @return {@code true} iff the location is loaded
	 */
	default boolean isLoaded() {
		return isBlockLoaded(getLocation());
	}

	/**
	 * Gets the block relevant in this context.
	 * 
	 * @return the block or {@code null} if the location is not loaded
	 */
	default B getBlock() {
		return getBlock(getLocation());
	}

	/**
	 * Gets the tile stack at the specified position; block location is implied
	 * by the context.
	 * 
	 * @return the specified tile stack or {@code null} if the location is not
	 *         loaded or the tile stack does not exist
	 */
	default TS getTilesOrNull(BlockFace face) {
		return getTilesOrNull(getLocation(), face);
	}

	/**
	 * Determines whether the location relevant to this context has a tile stack
	 * at the specified side.
	 * 
	 * @return {@code true} iff the tile stack exists
	 */
	default boolean hasTiles(BlockFace face) {
		return hasTiles(getLocation(), face);
	}

	/**
	 * Determines whether the specified position has a tile; block location is
	 * implied by the context.
	 * 
	 * @return {@code true} iff the tile exists
	 */
	default boolean hasTile(BlockFace face, int layer) {
		return hasTile(getLocation(), face, layer);
	}

	/**
	 * Gets the tile at the specified position; block location is implied by the
	 * context.
	 * 
	 * @return the specified tile or {@code null} if the location is not loaded
	 *         or the tile does not exist
	 */
	default T getTile(BlockFace face, int layer) {
		return getTile(getLocation(), face, layer);
	}

}
