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
import ru.windcorp.progressia.common.world.generic.context.WorldGenericContextWO;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;

public interface WorldDataContext
	extends WorldGenericContextWO<BlockData, TileData, EntityData>,
	WorldDataContextRO {

	/**
	 * Increases in-game time of this world by {@code change}. Total time is
	 * decreased when {@code change} is negative.
	 * 
	 * @param change the amount of time to add to current world time. May be
	 *               negative.
	 * @see #getTime()
	 */
	void advanceTime(float change);
	
	/*
	 * Subcontexting
	 */
	
	@Override
	BlockDataContext push(Vec3i location);
	
	@Override
	TileStackDataContext push(Vec3i location, RelFace face);
	
	@Override
	TileDataContext push(Vec3i location, RelFace face, int layer);

}
