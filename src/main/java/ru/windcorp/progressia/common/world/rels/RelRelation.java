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
import ru.windcorp.progressia.common.util.Vectors;

/**
 * Name stands for Relative Relation 
 */
public class RelRelation extends BlockRelation {
	
	private final Vec3i vector = new Vec3i();
	private final Vec3 floatVector = new Vec3();
	private final Vec3 normalized = new Vec3();
	
	private AbsRelation[] resolved = null;
	
	public RelRelation(int north, int west, int up) {
		this(north, west, up, false);
	}
	
	public RelRelation(Vec3i vector) {
		this(vector.x, vector.y, vector.z, false);
	}
	
	protected RelRelation(int north, int west, int up, boolean precomputeAllResolutions) {
		vector.set(north, west, up);
		floatVector.set(north, west, up);
		normalized.set(north, west, up);
		
		if (normalized.any()) {
			normalized.normalize();
		}
		
		if (precomputeAllResolutions) {
			for (AbsFace face : AbsFace.getFaces()) {
				resolve(face);
			}
		}
	}
	
	public static RelRelation of(Vec3i vector) {
		return of(vector.x, vector.y, vector.z);
	}
	
	public static RelRelation of(int north, int west, int up) {
		if (Math.abs(north) + Math.abs(west) + Math.abs(up) == 1) {
			if (up == 1) {
				return RelFace.UP;
			} else if (up == -1) {
				return RelFace.DOWN;
			} else if (north == 1) {
				return RelFace.NORTH;
			} else if (north == -1) {
				return RelFace.SOUTH;
			} else if (west == 1) {
				return RelFace.WEST;
			} else {
				assert west == -1;
				return RelFace.EAST;
			}
		}
		
		return new RelRelation(north, west, up);
	}
	
	/**
	 * @return the relative vector (northward, westward, upward)
	 */
	public Vec3i getRelVector() {
		return vector;
	}
	
	public Vec3 getRelFloatVector() {
		return floatVector;
	}
	
	public Vec3 getRelNormalized() {
		return normalized;
	}
	
	public int getNorthward() {
		return vector.x;
	}
	
	public int getWestward() {
		return vector.y;
	}
	
	public int getUpward() {
		return vector.z;
	}
	
	public int getSouthward() {
		return -getNorthward();
	}
	
	public int getEastward() {
		return -getWestward();
	}
	
	public int getDownward() {
		return -getUpward();
	}

	@Override
	public AbsRelation resolve(AbsFace up) {
		if (resolved == null) {
			resolved = new AbsRelation[AbsFace.BLOCK_FACE_COUNT];
		}
		
		if (resolved[up.getId()] == null) {
			resolved[up.getId()] = computeResolution(up);
		}
		
		return resolved[up.getId()];
	}
	
	private AbsRelation computeResolution(AbsFace up) {
		Vec3i resolution = Vectors.grab3i();
		AxisRotations.resolve(vector, up, resolution);
		AbsRelation result = AbsRelation.of(resolution);
		Vectors.release(resolution);
		return result;
	}

	@Override
	protected Vec3i getSample() {
		return getRelVector();
	}

}
