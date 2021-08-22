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

import java.util.Objects;

import static ru.windcorp.progressia.common.world.rels.AbsFace.BLOCK_FACE_COUNT;

public class BlockFaceResolver {
	
	/**
	 * A mapping from (up; relative) to absolute. Face IDs are used as keys.
	 */
	private static final AbsFace[][] RESOLUTION_TABLE = new AbsFace[BLOCK_FACE_COUNT][BLOCK_FACE_COUNT];
	
	/**
	 * A mapping from (up; absolute) to relative. Face IDs are used as keys.
	 */
	private static final RelFace[][] RELATIVIZATION_TABLE = new RelFace[BLOCK_FACE_COUNT][BLOCK_FACE_COUNT];
	
	static {
		for (AbsFace up : AbsFace.getFaces()) {
			for (RelFace relative : RelFace.getFaces()) {
				
				AbsFace absolute = (AbsFace) AbsRelation.of(AxisRotations.resolve(relative.getRelVector(), up, null));
				
				RESOLUTION_TABLE[up.getId()][relative.getId()] = absolute;
				RELATIVIZATION_TABLE[up.getId()][absolute.getId()] = relative;
				
			}
		}
	}

	public static AbsFace resolve(RelFace relative, AbsFace up) {
		Objects.requireNonNull(relative, "relative");
		Objects.requireNonNull(up, "up");
		
		if (relative == RelFace.UP) {
			return up;
		} else if (relative == RelFace.DOWN) {
			return up.getCounter();
		}
		
		return RESOLUTION_TABLE[up.getId()][relative.getId()];
	}
	
	public static RelFace relativize(AbsFace absolute, AbsFace up) {
		Objects.requireNonNull(absolute, "absolute");
		Objects.requireNonNull(up, "up");
		
		if (absolute == up) {
			return RelFace.UP;
		} else if (absolute.getCounter() == up) {
			return RelFace.DOWN;
		}
		
		return RELATIVIZATION_TABLE[up.getId()][absolute.getId()];
	}
	
	private BlockFaceResolver() {
	}

}
