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
package ru.windcorp.progressia.test.gen;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.GravityModel;
import ru.windcorp.progressia.common.world.rels.AbsFace;

public class TestGravityModel extends GravityModel {

	public TestGravityModel(String id) {
		super(id);
	}

	@Override
	protected void doGetGravity(Vec3 pos, Vec3 output) {
		output.set(0, 0, -9.8f);
	}
	
	@Override
	protected AbsFace doGetDiscreteUp(Vec3i chunkPos) {
		AbsFace rounded = AbsFace.roundToFace(chunkPos.x, chunkPos.y, chunkPos.z - 54);
		return rounded == null ? AbsFace.POS_Z : rounded;
	}

	@Override
	protected void doReadSettings(DataInput input) throws IOException, DecodingException {
		// Do nothing
	}

	@Override
	protected void doWriteSettings(DataOutput output) throws IOException {
		// Do nothing
	}

}
