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

package ru.windcorp.progressia.common.world.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.util.DataBuffer;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;

public class PacketSendEntity extends PacketAffectEntity {

	private String entityTypeId;
	private final DataBuffer buffer = new DataBuffer();

	public PacketSendEntity() {
		this("Core:SendEntity");
	}

	protected PacketSendEntity(String id) {
		super(id);
	}

	/**
	 * Returns the text ID of the entity added by this packet.
	 * 
	 * @return text ID
	 * @see #getEntityId()
	 */
	public String getEntityTypeId() {
		return entityTypeId;
	}

	public DataBuffer getBuffer() {
		return buffer;
	}

	public void set(EntityData entity) {
		super.set(entity.getEntityId());

		this.entityTypeId = entity.getId();

		try {
			entity.write(this.buffer.getWriter(), IOContext.COMMS);
		} catch (IOException e) {
			throw CrashReports.report(e, "Could not write an entity into an internal buffer");
		}
	}

	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		super.read(input);

		this.entityTypeId = input.readUTF();
		this.buffer.fill(input, input.readInt());
	}

	@Override
	public void write(DataOutput output) throws IOException {
		super.write(output);

		output.writeUTF(this.entityTypeId);
		output.writeInt(this.buffer.getSize());
		this.buffer.flush(output);
	}

	@Override
	public void apply(WorldData world) {
		EntityData entity = EntityDataRegistry.getInstance().create(getEntityTypeId());

		entity.setEntityId(getEntityId());
		try {
			entity.read(getBuffer().getReader(), IOContext.COMMS);
		} catch (IOException e) {
			throw CrashReports.report(e, "Could not read an entity from an internal buffer");
		}

		world.addEntity(entity);
	}

}
