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

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.comms.controls.ControlData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;

public class ControlPlaceTileData extends ControlData {

	private TileData tile;
	private final Vec3i blockInWorld = new Vec3i();
	private BlockFace face;

	public ControlPlaceTileData(String id) {
		super(id);
	}

	public TileData getTile() {
		return tile;
	}

	public Vec3i getBlockInWorld() {
		return blockInWorld;
	}

	public BlockFace getFace() {
		return face;
	}

	public void set(TileData block, Vec3i blockInWorld, BlockFace face) {
		this.tile = block;
		this.blockInWorld.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
		this.face = face;
	}

}
