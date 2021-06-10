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

package ru.windcorp.progressia.common.world.tile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class PacketAddTile extends PacketAffectTile {

	private String tileId;

	public PacketAddTile() {
		this("Core:AddTile");
	}

	protected PacketAddTile(String id) {
		super(id);
	}

	public String getTileId() {
		return tileId;
	}

	public void set(TileData tile, Vec3i blockInWorld, BlockFace face) {
		super.set(blockInWorld, face, -1);
		this.tileId = tile.getId();
	}

	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		super.read(input);
		this.tileId = input.readUTF();
	}

	@Override
	public void write(DataOutput output) throws IOException {
		super.write(output);
		output.writeUTF(this.tileId);
	}

	@Override
	public void apply(WorldData world) {
		TileData tile = TileDataRegistry.getInstance().get(getTileId());
		world.getTiles(getBlockInWorld(), getFace()).add(tile);
	}

}
