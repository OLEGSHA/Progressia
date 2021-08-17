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
package ru.windcorp.progressia.test.gen.surface.context;

import ru.windcorp.progressia.server.world.context.ServerTileContext;

public interface SurfaceTileContext extends ServerTileContext, SurfaceTileStackContext {

	public interface Logic extends ServerTileContext.Logic, SurfaceTileStackContext.Logic {

		@Override
		SurfaceTileContext data();
		
		@Override
		default SurfaceTileContext.Logic pushCloser() {
			return push(getLocation(), getFace(), getLayer() - 1);
		}
		
		@Override
		default SurfaceTileContext.Logic pushFarther() {
			return push(getLocation(), getFace(), getLayer() + 1);
		}

	}

	@Override
	SurfaceTileContext.Logic logic();
	
	@Override
	default SurfaceTileContext pushCloser() {
		return push(getLocation(), getFace(), getLayer() - 1);
	}
	
	@Override
	default SurfaceTileContext pushFarther() {
		return push(getLocation(), getFace(), getLayer() + 1);
	}

}
