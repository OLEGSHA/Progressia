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

package ru.windcorp.progressia.common.comms.controls;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ru.windcorp.progressia.common.comms.packets.Packet;
import ru.windcorp.progressia.common.world.DecodingException;

public class PacketControl extends Packet {

	private final ControlData control;

	public PacketControl(String id, ControlData control) {
		super(id);
		this.control = control;
	}

	public ControlData getControl() {
		return control;
	}

	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		// TODO implement controls
	}

	@Override
	public void write(DataOutput output) throws IOException {
		// implement controls
	}

}
