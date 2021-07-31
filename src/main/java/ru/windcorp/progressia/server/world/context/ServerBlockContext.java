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

import ru.windcorp.progressia.common.world.context.BlockDataContext;
import ru.windcorp.progressia.common.world.rels.BlockFace;
import ru.windcorp.progressia.server.world.TileLogicStack;

public interface ServerBlockContext extends BlockDataContext, ServerWorldContext, ServerBlockContextRO {

	public interface Logic extends ServerBlockContextRO.Logic, ServerWorldContext.Logic {

		@Override
		ServerBlockContext data();

		@Override
		default TileLogicStack getTilesOrNull(BlockFace face) {
			return (TileLogicStack) ServerBlockContextRO.Logic.super.getTilesOrNull(face);
		}

	}

	@Override
	ServerBlockContext.Logic logic();

}
