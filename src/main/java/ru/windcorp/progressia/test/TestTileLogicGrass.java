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

import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.BlockTickContext;
import ru.windcorp.progressia.server.world.ticking.TickingPolicy;
import ru.windcorp.progressia.server.world.tile.HangingTileLogic;
import ru.windcorp.progressia.server.world.tile.TickableTile;
import ru.windcorp.progressia.server.world.tile.TileTickContext;

public class TestTileLogicGrass extends HangingTileLogic implements TickableTile {

	public TestTileLogicGrass(String id) {
		super(id);
	}

	@Override
	public boolean canOccupyFace(TileTickContext context) {
		return context.getFace() != BlockFace.BOTTOM && super.canOccupyFace(context);
	}

	@Override
	public boolean canOccupyFace(BlockFace face) {
		return face != BlockFace.BOTTOM;
	}

	@Override
	public TickingPolicy getTickingPolicy(TileTickContext context) {
		return TickingPolicy.RANDOM;
	}

	@Override
	public void tick(TileTickContext context) {
		if (!isLocationSuitable(context)) {
			context.removeThisTile();
		}
	}

	@Override
	public boolean canBeSquashed(TileTickContext context) {
		return true;
	}

	private boolean isLocationSuitable(TileTickContext context) {
		return canOccupyFace(context) && isBlockAboveTransparent(context);
	}

	private boolean isBlockAboveTransparent(BlockTickContext context) {
		return context.evalNeighbor(BlockFace.TOP, bctxt -> {
			BlockLogic block = bctxt.getBlock();
			if (block == null)
				return true;

			return block.isTransparent(bctxt);
		});
	}

}
