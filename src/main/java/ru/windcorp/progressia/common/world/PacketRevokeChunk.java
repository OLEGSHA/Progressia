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
 
package ru.windcorp.progressia.common.world;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import glm.vec._3.i.Vec3i;

public class PacketRevokeChunk extends PacketAffectChunk {

	private final Vec3i position = new Vec3i();

	public PacketRevokeChunk() {
		this("Core:RevokeChunk");
	}

	protected PacketRevokeChunk(String id) {
		super(id);
	}

	public void set(Vec3i chunkPos) {
		this.position.set(chunkPos.x, chunkPos.y, chunkPos.z);
	}

	@Override
	public void read(DataInput input) throws IOException {
		this.position.set(input.readInt(), input.readInt(), input.readInt());
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.position.x);
		output.writeInt(this.position.y);
		output.writeInt(this.position.z);
	}

	@Override
	public void apply(DefaultWorldData world) {
		synchronized (world) {
			DefaultChunkData chunk = world.getChunk(position);
			if (chunk != null) {
				world.removeChunk(chunk);
			}
		}
	}

	@Override
	public void getAffectedChunk(Vec3i output) {
		output.set(getPosition().x, getPosition().y, getPosition().z);
	}

	public Vec3i getPosition() {
		return position;
	}

}
