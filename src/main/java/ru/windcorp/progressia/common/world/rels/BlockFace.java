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
package ru.windcorp.progressia.common.world.rels;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;

public interface BlockFace {
	
	public static final int BLOCK_FACE_COUNT = 6;
	
	AbsFace resolve(AbsFace up);
	RelFace relativize(AbsFace up);
	
	public default Vec3i getVector(AbsFace up) {
		return resolve(up).getVector();
	}
	
	public default Vec3 getFloatVector(AbsFace up) {
		return resolve(up).getFloatVector();
	}
	
	public default Vec3 getNormalized(AbsFace up) {
		return resolve(up).getNormalized();
	}

}
