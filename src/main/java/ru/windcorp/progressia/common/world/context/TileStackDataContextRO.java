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

import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.context.TileStackGenericContextRO;
import ru.windcorp.progressia.common.world.tile.TileData;

public interface TileStackDataContextRO
	extends TileStackGenericContextRO<BlockData, TileData, EntityData>,
	BlockDataContextRO {
	
	/*
	 * Subcontexting
	 */
	
	@Override
	default TileDataContextRO push(int layer) {
		return push(getLocation(), getFace(), layer);
	}
	
	@Override
	default TileStackDataContextRO pushCounter() {
		return push(getFace().getCounter());
	}
	
	@Override
	default TileStackDataContextRO pushOpposite() {
		return push(getLocation().add_(getFace().getRelVector()), getFace().getCounter());
	}

}
