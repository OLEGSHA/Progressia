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

package ru.windcorp.progressia.common.world.generic;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;

public interface GenericEntity {

	String getId();

	Vec3 getPosition();

	default Vec3i getBlockInWorld(Vec3i output) {
		if (output == null)
			output = new Vec3i();
		return getPosition().round(output);
	}

	default Vec3i getChunkCoords(Vec3i output) {
		output = getBlockInWorld(output);
		return Coordinates.convertInWorldToChunk(output, output);
	}

}
