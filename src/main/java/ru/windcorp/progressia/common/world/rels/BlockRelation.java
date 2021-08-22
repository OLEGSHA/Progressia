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

import static java.lang.Math.abs;
import static java.lang.Math.max;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;

public abstract class BlockRelation {

	public abstract AbsRelation resolve(AbsFace up);
	
	public Vec3i getVector(AbsFace up) {
		return resolve(up).getVector();
	}
	
	public Vec3 getFloatVector(AbsFace up) {
		return resolve(up).getFloatVector();
	}
	
	public Vec3 getNormalized(AbsFace up) {
		return resolve(up).getNormalized();
	}
	
	protected Vec3i getSample() {
		return getVector(AbsFace.POS_Z);
	}
	
	/**
	 * Returns the distance between the source and destination blocks, as
	 * defined by the Euclidean space. Your everyday distance.
	 * 
	 * @return square root of the sum of the squares of the coordinates
	 */
	public float getEuclideanDistance() {
		return getSample().length();
	}

	/**
	 * Returns the Manhattan distance, also known as the taxicab distance,
	 * between the source and the destination blocks. Manhattan distance is
	 * defined as the sum of the absolute values of the coordinates,
	 * which is also the minimum amount of block faces that need to be crossed
	 * to move from source to destination.
	 * 
	 * @return the sum of the absolute values of the coordinates
	 */
	public int getManhattanDistance() {
		Vec3i vector = getSample();
		return abs(vector.x) + abs(vector.y) + abs(vector.z);
	}

	/**
	 * Returns the Chebyshev distance between the source and the destination
	 * blocks. Chebyshev distance is defined as the maximum of the absolute
	 * values of the coordinates.
	 * 
	 * @return the maximum of the absolute values of the coordinates
	 */
	public int getChebyshevDistance() {
		Vec3i vector = getSample();
		return max(abs(vector.x), max(abs(vector.y), abs(vector.z)));
	}
	
}
