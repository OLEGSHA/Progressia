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
import ru.windcorp.progressia.common.world.GravityModel;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.context.WorldGenericContextRO;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.tile.TileData;

public interface WorldDataContextRO extends WorldGenericContextRO<BlockData, TileData, EntityData> {

	/**
	 * Returns in-world time since creation. World time is zero before and
	 * during first tick.
	 * <p>
	 * Game logic should assume that this value mostly increases uniformly.
	 * However, it is not guaranteed that in-world time always increments.
	 * 
	 * @return time, in in-game seconds, since the world was created
	 */
	float getTime();

	/**
	 * Gets the {@link GravityModel} used by this world.
	 * 
	 * @return the gravity model
	 */
	GravityModel getGravityModel();
	
	/*
	 * Subcontexting
	 */
	
	@Override
	BlockDataContextRO push(Vec3i location);
	
	@Override
	TileStackDataContextRO push(Vec3i location, RelFace face);
	
	@Override
	TileDataContextRO push(Vec3i location, RelFace face, int layer);

}
