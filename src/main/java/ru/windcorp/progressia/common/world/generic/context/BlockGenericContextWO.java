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
import ru.windcorp.progressia.common.world.generic.*;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.rels.RelRelation;

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
	E  extends EntityGeneric
> extends WorldContexts.Block, WorldGenericContextWO<B, T, E> {
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
	default void addTile(RelFace face, T tile) {
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
	default void removeTile(RelFace face, int tag) {
		removeTile(getLocation(), face, tag);
	}
	
	/*
	 * Subcontexting
	 */
	
	@Override
	default BlockGenericContextWO<B, T, E> pushRelative(int dx, int dy, int dz) {
		return push(getLocation().add_(dx, dy, dz));
	}
	
	@Override
	default BlockGenericContextWO<B, T, E> pushRelative(Vec3i direction) {
		return push(getLocation().add_(direction));
	}
	
	@Override
	default BlockGenericContextWO<B, T, E> pushRelative(RelRelation direction) {
		return push(getLocation().add_(direction.getRelVector()));
	}
	
	@Override
	default TileStackGenericContextWO<B, T, E> push(RelFace face) {
		return push(getLocation(), face);
	}
	
	@Override
	default TileGenericContextWO<B, T, E> push(RelFace face, int layer) {
		return push(getLocation(), face, layer);
	}

}
