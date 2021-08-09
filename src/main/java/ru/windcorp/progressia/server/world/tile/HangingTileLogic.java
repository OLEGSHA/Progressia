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
 
package ru.windcorp.progressia.server.world.tile;

import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.context.ServerTileContext;
import ru.windcorp.progressia.server.world.context.ServerTileContextRO;

public class HangingTileLogic extends TileLogic implements UpdateableTile {

	public HangingTileLogic(String id) {
		super(id);
	}

	@Override
	public void update(ServerTileContext context) {
		if (!canOccupyFace(context)) {
			context.removeTile();
		}
	}

	@Override
	public boolean canOccupyFace(ServerTileContextRO context) {
		BlockLogic host = context.logic().getBlock();
		if (host == null)
			return false;

		if (!host.isSolid(context, context.getFace()))
			return false;

		if (canBeSquashed(context))
			return true;

		context.pushOpposite();
		BlockLogic complHost = context.logic().getBlock();
		boolean result = complHost == null || !complHost.isSolid(context, context.getFace());
		context.pop();
		return result;
	}

	public boolean canBeSquashed(ServerTileContextRO context) {
		return false;
	}

}
