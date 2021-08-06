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
package ru.windcorp.progressia.server.world.context;

import ru.windcorp.progressia.common.world.context.TileStackDataContextRO;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.context.TileStackGenericContextRO;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.tile.TileLogic;

public interface ServerTileStackContextRO extends ServerBlockContextRO, TileStackDataContextRO {

	public interface Logic
		extends ServerBlockContextRO.Logic, TileStackGenericContextRO<BlockLogic, TileLogic, EntityData> {

		@Override
		ServerTileStackContextRO data();
		
		@Override
		default ServerTileContextRO.Logic push(int layer) {
			return push(getLocation(), getFace(), layer);
		}
		
		@Override
		default ServerTileStackContextRO.Logic pushCounter() {
			return push(getFace().getCounter());
		}
		
		@Override
		default ServerTileStackContextRO.Logic pushOpposite() {
			return push(getLocation().add_(getFace().getRelVector()), getFace().getCounter());
		}

	}

	@Override
	ServerTileStackContextRO.Logic logic();
	
	@Override
	default ServerTileContextRO push(int layer) {
		return push(getLocation(), getFace(), layer);
	}
	
	@Override
	default ServerTileStackContextRO pushCounter() {
		return push(getFace().getCounter());
	}
	
	@Override
	default ServerTileStackContextRO pushOpposite() {
		return push(getLocation().add_(getFace().getRelVector()), getFace().getCounter());
	}

}
