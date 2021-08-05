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
 * A writable {@link Context} referencing a world with a block location, a block
 * face and a tile layer specified, effectively pointing to a single tile. This
 * context provides methods for affecting the world. The application of
 * requested changes may or may not be immediate, see {@link #isImmediate()}.
 * The tile may or may not actually exist.
 */
//@formatter:off
public interface TileGenericContextWO<
	B  extends BlockGeneric,
	T  extends TileGeneric,
	E  extends EntityGeneric
> extends WorldContexts.Tile, BlockFaceGenericContextWO<B, T, E> {
//@formatter:on

	/**
	 * Requests that the tile relevant to this context be removed from its tile
	 * stack. If the tile could not be found at the time of application this
	 * method fails silently.
	 */
	default void removeTile() {
		removeTile(getLocation(), getFace(), getTag());
	}

}
