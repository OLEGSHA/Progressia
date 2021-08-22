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
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.DefaultWorldData;
import ru.windcorp.progressia.common.world.TileDataStack;
import ru.windcorp.progressia.common.world.rels.AbsFace;

public class PacketRemoveTile extends PacketAffectTile {

	public PacketRemoveTile() {
		this("Core:RemoveTile");
	}

	protected PacketRemoveTile(String id) {
		super(id);
	}

	@Override
	public void set(Vec3i blockInWorld, AbsFace face, int tag) {
		super.set(blockInWorld, face, tag);
	}

	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		super.read(input);
	}

	@Override
	public void write(DataOutput output) throws IOException {
		super.write(output);
	}

	@Override
	public void apply(DefaultWorldData world) {
		TileDataStack stack = world.getTiles(getBlockInWorld(), getFace());

		int index = stack.getIndexByTag(getTag());

		if (index < 0) {
			throw CrashReports.report(
				null,
				"Could not find tile with tag %d at (%d; %d; %d; %s)",
				getTag(),
				getBlockInWorld().x,
				getBlockInWorld().y,
				getBlockInWorld().z,
				getFace()
			);
		}

		stack.remove(index);
	}

}
