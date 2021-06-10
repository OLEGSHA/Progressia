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

package ru.windcorp.progressia.common.world.block;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;

public class PacketSetBlock extends PacketAffectBlock {

	private String blockId;

	public PacketSetBlock() {
		this("Core:SetBlock");
	}

	protected PacketSetBlock(String id) {
		super(id);
	}

	public String getBlockId() {
		return blockId;
	}

	public void set(BlockData block, Vec3i blockInWorld) {
		super.set(blockInWorld);
		this.blockId = block.getId();
	}

	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		super.read(input);
		this.blockId = input.readUTF();
	}

	@Override
	public void write(DataOutput output) throws IOException {
		super.write(output);
		output.writeUTF(this.blockId);
	}

	@Override
	public void apply(WorldData world) {
		BlockData block = BlockDataRegistry.getInstance().get(getBlockId());
		world.setBlock(getBlockInWorld(), block, true);
	}

}
