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
import ru.windcorp.progressia.common.world.rels.RelFace;

/**
 * This class defines several {@link Context} subinterfaces that are further
 * extended by Generic contexts. These interfaces declare methods for
 * determining which location is "relevant" to the context. Since they are not
 * Java generics they can safely be extended more than once.
 * <p>
 * Do not reuse these interfaces outside the Generic contexts' package; consider
 * them to be an implementation detail.
 * 
 * @author javapony
 *
 */
class WorldContexts {

	/**
	 * A {@link Context} with a world instance. This interface should not be
	 * implemented directly; see {@link WorldGenericContextRO} or
	 * {@link WorldGenericContextWO}.
	 * 
	 * @author javapony
	 *
	 */
	public static interface World extends Context {

		// currently empty

	}

	/**
	 * A {@link Context} with a world instance and a block location. This interface
	 * should not be implemented directly; see {@link BlockGenericContextRO} or
	 * {@link BlockGenericContextWO}.
	 * 
	 * @author javapony
	 *
	 */
	public static interface Block extends World {

		/**
		 * Returns the location of the block.
		 * <p>
		 * The coordinate system in use is not specified, but it is consistent across
		 * all methods of this context.
		 * <p>
		 * The object returned by this method must not be modified. It is only valid
		 * while the context is {@linkplain valid}.
		 * 
		 * @return a vector describing the block's position
		 */
		Vec3i getLocation();

	}

	/**
	 * A {@link Context} with a world instance, a block location and a block face
	 * (block side). This interface should not be implemented directly; see
	 * {@link TileStackGenericContextRO} or {@link TileStackGenericContextWO}.
	 * 
	 * @author javapony
	 *
	 */
	public static interface BlockFace extends Block {

		/**
		 * Returns the face relevant to this context.
		 * 
		 * @return the block face
		 */
		RelFace getFace();

	}

	/**
	 * A {@link Context} with a world instance, a block location, a block face
	 * (block side) and a tile layer. This interface should not be implemented
	 * directly; see {@link TileGenericContextRO} or {@link TileGenericContextWO}.
	 * 
	 * @author javapony
	 *
	 */
	public static interface Tile extends BlockFace {

		/**
		 * Returns the tile layer relevant to this context.
		 * 
		 * @return the tile layer
		 */
		int getLayer();

		/**
		 * Gets the tag of the tile at the relevant position.
		 * 
		 * @return the tag of the tile or {@code -1} if the location is not loaded
		 *         or the tile does not exist
		 */
		int getTag();

	}

	WorldContexts() {
	}

}
