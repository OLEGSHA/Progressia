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

import ru.windcorp.progressia.common.world.context.TileDataContextRO;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.context.TileGenericContextRO;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.tile.TileLogic;

public interface ServerTileContextRO extends ServerTileStackContextRO, TileDataContextRO {

	public interface Logic
		extends ServerTileStackContextRO.Logic, TileGenericContextRO<BlockLogic, TileLogic, EntityData> {

		@Override
		ServerTileContextRO data();
		
		@Override
		default ServerTileContextRO.Logic pushCloser() {
			return push(getLocation(), getFace(), getLayer() - 1);
		}
		
		@Override
		default ServerTileContextRO.Logic pushFarther() {
			return push(getLocation(), getFace(), getLayer() + 1);
		}

	}

	@Override
	ServerTileContextRO.Logic logic();
	
	@Override
	default ServerTileContextRO pushCloser() {
		return push(getLocation(), getFace(), getLayer() - 1);
	}
	
	@Override
	default ServerTileContextRO pushFarther() {
		return push(getLocation(), getFace(), getLayer() + 1);
	}

}
