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

import ru.windcorp.progressia.common.world.context.TileStackDataContext;

public interface ServerTileStackContext extends TileStackDataContext, ServerBlockContext, ServerTileStackContextRO {

	public interface Logic extends ServerTileStackContextRO.Logic, ServerBlockContext.Logic {

		@Override
		ServerTileStackContext data();
		
		@Override
		default ServerTileContext.Logic push(int layer) {
			return push(getLocation(), getFace(), layer);
		}
		
		@Override
		default ServerTileStackContext.Logic pushCounter() {
			return push(getFace().getCounter());
		}
		
		@Override
		default ServerTileStackContext.Logic pushOpposite() {
			return push(getLocation().add_(getFace().getRelVector()), getFace().getCounter());
		}

	}

	@Override
	ServerTileStackContext.Logic logic();
	
	@Override
	default ServerTileContext push(int layer) {
		return push(getLocation(), getFace(), layer);
	}
	
	@Override
	default ServerTileStackContext pushCounter() {
		return push(getFace().getCounter());
	}
	
	@Override
	default ServerTileStackContext pushOpposite() {
		return push(getLocation().add_(getFace().getRelVector()), getFace().getCounter());
	}

}
