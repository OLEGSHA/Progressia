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
package ru.windcorp.progressia.common.world.context;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.context.BlockGenericContextWO;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.rels.RelRelation;
import ru.windcorp.progressia.common.world.tile.TileData;

public interface BlockDataContext
	extends BlockGenericContextWO<BlockData, TileData, EntityData>,
	WorldDataContext,
	BlockDataContextRO {

	/*
	 * Subcontexting
	 */
	
	@Override
	default BlockDataContext pushRelative(int dx, int dy, int dz) {
		return push(getLocation().add_(dx, dy, dz));
	}
	
	@Override
	default BlockDataContext pushRelative(Vec3i direction) {
		return push(getLocation().add_(direction));
	}
	
	@Override
	default BlockDataContext pushRelative(RelRelation direction) {
		return push(getLocation().add_(direction.getRelVector()));
	}
	
	@Override
	default TileStackDataContext push(RelFace face) {
		return push(getLocation(), face);
	}
	
	@Override
	default TileDataContext push(RelFace face, int layer) {
		return push(getLocation(), face, layer);
	}

}
