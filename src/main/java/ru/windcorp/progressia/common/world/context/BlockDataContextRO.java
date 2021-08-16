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
import ru.windcorp.progressia.common.world.generic.context.BlockGenericContextRO;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.rels.RelRelation;
import ru.windcorp.progressia.common.world.tile.TileData;

public interface BlockDataContextRO
	extends BlockGenericContextRO<BlockData, TileData, EntityData>,
	WorldDataContextRO {

	/*
	 * Subcontexting
	 */
	
	@Override
	default BlockDataContextRO pushRelative(int dx, int dy, int dz) {
		return push(getLocation().add_(dx, dy, dz));
	}
	
	@Override
	default BlockDataContextRO pushRelative(Vec3i direction) {
		return push(getLocation().add_(direction));
	}
	
	@Override
	default BlockDataContextRO pushRelative(RelRelation direction) {
		return push(getLocation().add_(direction.getRelVector()));
	}
	
	@Override
	default TileStackDataContextRO push(RelFace face) {
		return push(getLocation(), face);
	}
	
	@Override
	default TileDataContextRO push(RelFace face, int layer) {
		return push(getLocation(), face, layer);
	}

}
