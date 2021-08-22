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
package ru.windcorp.progressia.common.world.generic;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.state.StateChange;
import ru.windcorp.progressia.common.state.StatefulObject;
import ru.windcorp.progressia.common.world.rels.BlockFace;

//@formatter:off
public interface WorldGenericWO<
	B  extends BlockGeneric,
	T  extends TileGeneric,
	TS extends TileGenericStackWO     <B, T, TS, TR, C>,
	TR extends TileGenericReferenceWO <B, T, TS, TR, C>,
	C  extends ChunkGenericWO         <B, T, TS, TR, C>,
	E  extends EntityGeneric
> {
//@formatter:on

	void setBlock(Vec3i blockInWorld, B block, boolean notify);

	TS getTiles(Vec3i blockInWorld, BlockFace face);

	void addEntity(E entity);

	void removeEntity(long entityId);

	default void removeEntity(E entity) {
		removeEntity(entity.getEntityId());
	}

	/**
	 * Requests that the specified change is applied to the given entity. The
	 * {@code change} object provided may be stored until the change is applied.
	 * 
	 * @param entity the entity to change
	 * @param change the change to apply
	 */
	<SE extends StatefulObject & EntityGeneric> void changeEntity(SE entity, StateChange<SE> change);

}
