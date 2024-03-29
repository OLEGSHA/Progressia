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

import ru.windcorp.progressia.common.world.context.TileDataContext;

public interface ServerTileContext extends TileDataContext, ServerTileStackContext, ServerTileContextRO {

	public interface Logic extends ServerTileContextRO.Logic, ServerTileStackContext.Logic {

		@Override
		ServerTileContext data();
		
		@Override
		default ServerTileContext.Logic pushCloser() {
			return push(getLocation(), getFace(), getLayer() - 1);
		}
		
		@Override
		default ServerTileContext.Logic pushFarther() {
			return push(getLocation(), getFace(), getLayer() + 1);
		}

	}

	@Override
	ServerTileContext.Logic logic();
	
	@Override
	default ServerTileContext pushCloser() {
		return push(getLocation(), getFace(), getLayer() - 1);
	}
	
	@Override
	default ServerTileContext pushFarther() {
		return push(getLocation(), getFace(), getLayer() + 1);
	}

}
