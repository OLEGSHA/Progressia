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

import ru.windcorp.progressia.common.util.DataBuffer;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class PacketSetGravityModel extends PacketAffectWorld {
	
	private String gravityModelId;
	private final DataBuffer settings = new DataBuffer();
	
	public PacketSetGravityModel() {
		this("Core:SetGravityModel");
	}

	protected PacketSetGravityModel(String id) {
		super(id);
	}
	
	public void set(GravityModel model) {
		this.gravityModelId = model.getId();
		
		try {
			model.writeSettings(settings.getWriter());
		} catch (IOException e) {
			throw CrashReports.report(e, "%s has errored when writing its settings", model);
		}
	}

	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		gravityModelId = input.readUTF();
		settings.fill(input, input.readInt());
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeUTF(gravityModelId);
		output.writeInt(settings.getSize());
		settings.flush(output);
	}

	@Override
	public void apply(WorldData world) {
		GravityModel model = GravityModelRegistry.getInstance().create(gravityModelId);
		world.setGravityModel(model);
		try {
			model.readSettings(settings.getReader());
		} catch (IOException e) {
			throw CrashReports.report(e, "%s has errored when reading its settings", model);
		} catch (DecodingException e) {
			throw CrashReports.report(e, "%s has failed to parse its settings", model);
		}
	}

}
