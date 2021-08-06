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
import ru.windcorp.progressia.common.world.rels.AbsRelation;
import ru.windcorp.progressia.common.world.rels.RelFace;

/**
 * This class defines several {@link Context} subinterfaces that are further
 * extended by Generic contexts. These interfaces declare methods for
 * determining which location is "relevant" to the context and the basic
 * subcontexting methods. Since they are not Java generics they can safely be
 * extended more than once.
 * <p>
 * Do not reuse these interfaces outside the Generic contexts' package; consider
 * them to be an implementation detail.
 * 
 * @author javapony
 */
class WorldContexts {

	/**
	 * A {@link Context} with a world instance. This interface should not be
	 * implemented directly; see {@link WorldGenericContextRO} or
	 * {@link WorldGenericContextWO}.
	 * 
	 * @author javapony
	 */
	public static interface World extends Context {

		/**
		 * Assigns the specified location to this context. Block face and tile
		 * layer information is discarded if it was present. See
		 * {@linkplain Context#subcontexting subcontexting} for more details.
		 * 
		 * @param location the new location to use
		 * @return this object
		 * @see #pop()
		 */
		Block push(Vec3i location);

		/**
		 * Assigns the specified location and block face to this context. Tile
		 * layer information is discarded if it was present. See
		 * {@linkplain Context#subcontexting subcontexting} for more details.
		 * 
		 * @param location the new location to use
		 * @param face     the new block face to use
		 * @return this object
		 * @see #pop()
		 */
		TileStack push(Vec3i location, RelFace face);

		/**
		 * Assigns the specified position to this context. See
		 * {@linkplain Context#subcontexting subcontexting} for more details.
		 * 
		 * @param location the new location to use
		 * @param face     the new block face to use
		 * @param layer    the new tile layer to use
		 * @return this object
		 * @see #pop()
		 */
		Tile push(Vec3i location, RelFace face, int layer);

	}

	/**
	 * A {@link Context} with a world instance and a block location. This
	 * interface
	 * should not be implemented directly; see {@link BlockGenericContextRO} or
	 * {@link BlockGenericContextWO}.
	 * 
	 * @author javapony
	 */
	public static interface Block extends World {

		/**
		 * Returns the location of the block.
		 * <p>
		 * The coordinate system in use is not specified, but it is consistent
		 * across
		 * all methods of this context.
		 * <p>
		 * The object returned by this method must not be modified. It is only
		 * valid
		 * while the context is {@linkplain valid}.
		 * 
		 * @return a vector describing the block's position
		 */
		Vec3i getLocation();

		/**
		 * Shifts the location in the specified direction. Block face and tile
		 * layer information is discarded if it was present. See
		 * {@linkplain Context#subcontexting subcontexting} for more details.
		 * 
		 * @param dx the change of the <i>x</i> component
		 * @param dy the change of the <i>y</i> component
		 * @param dz the change of the <i>z</i> component
		 * @return this object
		 * @see #pop()
		 */
		default Block pushRelative(int dx, int dy, int dz) {
			return push(getLocation().add_(dx, dy, dz));
		}

		/**
		 * Shifts the location in the specified direction. Block face and tile
		 * layer information is discarded if it was present. See
		 * {@linkplain Context#subcontexting subcontexting} for more details.
		 * 
		 * @param direction the change added to the current location
		 * @return this object
		 * @see #pop()
		 */
		default Block pushRelative(Vec3i direction) {
			return push(getLocation().add_(direction));
		}

		/**
		 * Shifts the location in the specified direction. Block face and tile
		 * layer information is discarded if it was present. See
		 * {@linkplain Context#subcontexting subcontexting} for more details.
		 * 
		 * @param direction the change added to the current location
		 * @return this object
		 * @see #pop()
		 */
		default Block pushRelative(AbsRelation direction) {
			return push(direction.getVector());
		}

		/**
		 * Assigns the specified block face to this context. Tile layer
		 * information is discarded if it was present. See
		 * {@linkplain Context#subcontexting subcontexting} for more details.
		 * 
		 * @param face the new block face to use
		 * @return this object
		 * @see #pop()
		 */
		default TileStack push(RelFace face) {
			return push(getLocation(), face);
		}

		/**
		 * Assigns the specified block face and tile layer to this context. See
		 * {@linkplain Context#subcontexting subcontexting} for more details.
		 * 
		 * @param face  the new block face to use
		 * @param layer the new tile layer to use
		 * @return this object
		 * @see #pop()
		 */
		default Tile push(RelFace face, int layer) {
			return push(getLocation(), face, layer);
		}

	}

	/**
	 * A {@link Context} with a world instance, a block location and a block
	 * face
	 * (block side). This interface should not be implemented directly; see
	 * {@link TileStackGenericContextRO} or {@link TileStackGenericContextWO}.
	 * 
	 * @author javapony
	 */
	public static interface TileStack extends Block {

		/**
		 * Returns the face relevant to this context.
		 * 
		 * @return the block face
		 */
		RelFace getFace();

		/**
		 * Assigns the specified tile layer to this context. See
		 * {@linkplain Context#subcontexting subcontexting} for more details.
		 * 
		 * @param layer the new tile layer to use
		 * @return this object
		 * @see #pop()
		 */
		default Tile push(int layer) {
			return push(getLocation(), getFace(), layer);
		}

		/**
		 * Assigns the counter face (the face on the opposite side of this
		 * block) to this context. Tile layer information is discarded if it was
		 * present. See {@linkplain Context#subcontexting subcontexting} for
		 * more details.
		 * 
		 * @return this object
		 * @see #pop()
		 */
		default TileStack pushCounter() {
			return push(getFace().getCounter());
		}

		/**
		 * Assigns the face opposite to the current face to this context. The
		 * current face and its opposite are the only two tile stacks occupying
		 * the gap between the two respective blocks. Tile layer information is
		 * discarded if it was present. See {@linkplain Context#subcontexting
		 * subcontexting} for
		 * more details.
		 * 
		 * @return this object
		 * @see #pop()
		 */
		default TileStack pushOpposite() {
			return push(getLocation().add_(getFace().getRelVector()), getFace().getCounter());
		}

	}

	/**
	 * A {@link Context} with a world instance, a block location, a block face
	 * (block side) and a tile layer. This interface should not be implemented
	 * directly; see {@link TileGenericContextRO} or
	 * {@link TileGenericContextWO}.
	 * 
	 * @author javapony
	 */
	public static interface Tile extends TileStack {

		/**
		 * Returns the tile layer relevant to this context.
		 * 
		 * @return the tile layer
		 */
		int getLayer();

		/**
		 * Gets the tag of the tile at the relevant position.
		 * 
		 * @return the tag of the tile or {@code -1} if the location is not
		 *         loaded
		 *         or the tile does not exist
		 */
		int getTag();

		/**
		 * Assigns the tile layer closer to the host block to this context. See
		 * {@linkplain Context#subcontexting subcontexting} for more details.
		 * 
		 * @return this object
		 * @see #pop()
		 */
		default Tile pushCloser() {
			return push(getLocation(), getFace(), getLayer() - 1);
		}

		/**
		 * Assigns the tile layer farther to the host block to this context. See
		 * {@linkplain Context#subcontexting subcontexting} for more details.
		 * 
		 * @return this object
		 * @see #pop()
		 */
		default Tile pushFarther() {
			return push(getLocation(), getFace(), getLayer() + 1);
		}

	}

	WorldContexts() {
	}

}
