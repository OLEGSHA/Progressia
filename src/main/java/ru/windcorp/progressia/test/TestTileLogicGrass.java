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
 
package ru.windcorp.progressia.test;

import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.context.ServerTileContext;
import ru.windcorp.progressia.server.world.context.ServerTileContextRO;
import ru.windcorp.progressia.server.world.ticking.TickingPolicy;
import ru.windcorp.progressia.server.world.tile.HangingTileLogic;
import ru.windcorp.progressia.server.world.tile.TickableTile;

public class TestTileLogicGrass extends HangingTileLogic implements TickableTile {

	public TestTileLogicGrass(String id) {
		super(id);
	}

	@Override
	public boolean canOccupyFace(ServerTileContextRO context) {
		return context.getFace() != RelFace.DOWN && super.canOccupyFace(context);
	}

	@Override
	public boolean canOccupyFace(RelFace face) {
		return face != RelFace.DOWN;
	}

	@Override
	public TickingPolicy getTickingPolicy(ServerTileContextRO context) {
		return TickingPolicy.RANDOM;
	}

	@Override
	public void tick(ServerTileContext context) {
		if (!isLocationSuitable(context)) {
			context.removeTile();
		}
	}

	@Override
	public boolean canBeSquashed(ServerTileContextRO context) {
		return true;
	}

	private boolean isLocationSuitable(ServerTileContextRO context) {
		return canOccupyFace(context) && isBlockAboveTransparent(context);
	}

	private boolean isBlockAboveTransparent(ServerTileContextRO context) {
		// TODO rework
		context.pushRelative(RelFace.UP.resolve(AbsFace.POS_Z));
		BlockLogic block = context.logic().getBlock();
		return context.popAndReturn(block == null || block.isTransparent(context));
	}

}
