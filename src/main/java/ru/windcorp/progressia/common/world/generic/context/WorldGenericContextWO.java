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
import ru.windcorp.progressia.common.state.StateChange;
import ru.windcorp.progressia.common.state.StatefulObject;
import ru.windcorp.progressia.common.world.context.Context;
import ru.windcorp.progressia.common.world.generic.*;
import ru.windcorp.progressia.common.world.rels.BlockFace;

/**
 * A writable {@link Context} with a world instance. This context provides
 * methods for affecting the world. The application of requested changes may or
 * may not be immediate, see {@link #isImmediate()}.
 */
// @formatter:off
public interface WorldGenericContextWO<
	B  extends BlockGeneric,
	T  extends TileGeneric,
	TS extends TileGenericStackWO     <B, T, TS, TR, C>,
	TR extends TileGenericReferenceWO <B, T, TS, TR, C>,
	C  extends ChunkGenericWO         <B, T, TS, TR, C>,
	E  extends EntityGeneric
> extends WorldContexts.World, WorldGenericWO<B, T, TS, TR, C, E> {
// @formatter:on

	/**
	 * Queries whether changes requested with this context are guaranteed to be
	 * applied immediately.
	 * <p>
	 * When the changes are applied immediately, all subsequent queries will
	 * reflect the change. When the changes are not applied immediately, none of
	 * the subsequent queries will be affected by the requests while the context
	 * is {@linkplain Context#validity valid}. Immediate mode does not change
	 * while the context is valid.
	 * 
	 * @return {@code true} iff changes are visible immediately
	 */
	boolean isImmediate();

	/**
	 * Requests that a block is changed. The object provided may be stored until
	 * the change is applied.
	 * 
	 * @param location the location of the change
	 * @param block    the new block
	 * @see #isImmediate()
	 */
	default void setBlock(Vec3i location, B block) {
		setBlock(location, block);
	}

	/**
	 * Requests that a tile is added to the top of the tile stack at the given
	 * location. The object provided may be stored until the change is applied.
	 * If the tile could not be added at the time of application this method
	 * fails silently.
	 * 
	 * @param location the location of the block to which the tile is to be
	 *                 added
	 * @param face     the face of the block to add the tile to
	 * @param tile     the tile to add
	 */
	void addTile(Vec3i location, BlockFace face, T tile);

	/**
	 * Requests that a tile identified by its tag is removed from the specified
	 * tile stack. If the tile could not be found at the time of application
	 * this method fails silently.
	 * 
	 * @param location the location of the block from which the tile is to be
	 *                 removed
	 * @param face     the of the block to remove the tile from
	 * @param tag      the tag of the tile to remove
	 */
	void removeTile(Vec3i location, BlockFace face, int tag);

	/**
	 * Requests that the referenced tile is removed from its tile stack. If the
	 * tile could not be found at the time of application this method fails
	 * silently.
	 * 
	 * @param tileReference a reference to the tile
	 */
	default void removeTile(TileGenericReferenceRO<?, ?, ?, ?, ?> tileReference) {
		TileGenericStackRO<?, ?, ?, ?, ?> tileStack = tileReference.getStack();

		if (tileStack == null) {
			return;
		}

		removeTile(tileStack.getBlockInWorld(null), tileStack.getFace(), tileReference.getTag());
	}

	/**
	 * Requests that an entity is added to the world. The object provided may be
	 * stored until the change is applied. If the entity was already added to
	 * the world at the time of application this method does nothing.
	 * 
	 * @param entity the entity to add
	 * @see #isImmediate()
	 */
	@Override
	void addEntity(E entity);

	/**
	 * Requests that an entity with the given entity ID is removed from the
	 * world. If the entity did not exist at the time of application this method
	 * fails silently.
	 * 
	 * @param entityId the ID of the entity to remove
	 * @see #isImmediate()
	 * @see #removeEntity(EntityGeneric)
	 */
	@Override
	void removeEntity(long entityId);

	/**
	 * Requests that the entity is removed from the world. If the entity did not
	 * exist at the time of application this method fails silently.
	 * 
	 * @param entity the entity to remove
	 * @see #isImmediate()
	 * @see #removeEntity(long)
	 */
	@Override
	void removeEntity(E entity);

	/**
	 * Requests that the specified change is applied to the given entity. The
	 * {@code change} object provided may be stored until the change is applied.
	 * 
	 * @param entity the entity to change
	 * @param change the change to apply
	 */
	@Override
	<SE extends StatefulObject & EntityGeneric> void changeEntity(SE entity, StateChange<SE> change);

}
