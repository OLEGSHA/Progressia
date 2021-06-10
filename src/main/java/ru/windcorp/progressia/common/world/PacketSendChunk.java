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
import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.util.DataBuffer;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.io.ChunkIO;

public class PacketSendChunk extends PacketAffectChunk {

	private final DataBuffer data = new DataBuffer();
	private final Vec3i position = new Vec3i();

	public PacketSendChunk() {
		this("Core:SendChunk");
	}

	protected PacketSendChunk(String id) {
		super(id);
	}

	public void set(ChunkData chunk) {
		this.position.set(chunk.getX(), chunk.getY(), chunk.getZ());

		try {
			ChunkIO.save(chunk, this.data.getWriter(), IOContext.COMMS);
		} catch (IOException e) {
			// Impossible
		}
	}

	@Override
	public void read(DataInput input) throws IOException {
		this.position.set(input.readInt(), input.readInt(), input.readInt());
		this.data.fill(input, input.readInt());
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.position.x);
		output.writeInt(this.position.y);
		output.writeInt(this.position.z);
		output.writeInt(this.data.getSize());
		this.data.flush(output);
	}

	@Override
	public void apply(WorldData world) {
		try {
			world.addChunk(ChunkIO.load(world, position, data.getReader(), IOContext.COMMS));
		} catch (DecodingException | IOException e) {
			throw CrashReports.report(e, "Could not load chunk");
		}
	}

	@Override
	public void getAffectedChunk(Vec3i output) {
		output.set(getPosition().x, getPosition().y, getPosition().z);
	}

	public Vec3i getPosition() {
		return position;
	}

	public DataBuffer getData() {
		return data;
	}

}
