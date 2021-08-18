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
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.PacketAffectChunk;
import ru.windcorp.progressia.common.world.block.BlockFace;

public abstract class PacketAffectTile extends PacketAffectChunk {

	private final Vec3i blockInWorld = new Vec3i();
	private BlockFace face;
	private int tag;

	public PacketAffectTile(String id) {
		super(id);
	}

	public Vec3i getBlockInWorld() {
		return blockInWorld;
	}

	public BlockFace getFace() {
		return face;
	}

	public int getTag() {
		return tag;
	}

	public void set(Vec3i blockInWorld, BlockFace face, int tag) {
		this.blockInWorld.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
		this.face = face;
		this.tag = tag;
	}

	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		this.blockInWorld.set(input.readInt(), input.readInt(), input.readInt());
		this.face = BlockFace.getFaces().get(input.readByte());
		this.tag = input.readInt();
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.blockInWorld.x);
		output.writeInt(this.blockInWorld.y);
		output.writeInt(this.blockInWorld.z);
		output.writeByte(this.face.getId());
		output.writeInt(this.tag);
	}

	@Override
	public void getAffectedChunk(Vec3i output) {
		Coordinates.convertInWorldToChunk(this.blockInWorld, output);
	}

}
