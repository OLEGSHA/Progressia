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

public class AbsRelation extends BlockRelation {

	private final Vec3i vector = new Vec3i();
	private final Vec3 floatVector = new Vec3();
	private final Vec3 normalized = new Vec3();

	public AbsRelation(int x, int y, int z) {
		vector.set(x, y, z);
		floatVector.set(x, y, z);
		normalized.set(x, y, z);
		
		if (x != 0 || y != 0 || z != 0) {
			normalized.normalize();
		}
	}

	public AbsRelation(Vec3i vector) {
		this(vector.x, vector.y, vector.z);
	}
	
	public static AbsRelation of(Vec3i vector) {
		return of(vector.x, vector.y, vector.z);
	}
	
	public static AbsRelation of(int x, int y, int z) {
		if (Math.abs(x) + Math.abs(y) + Math.abs(z) == 1) {
			return AbsFace.roundToFace(x, y, z);
		}
		
		return new AbsRelation(x, y, z);
	}
	
	@Override
	public AbsRelation resolve(AbsFace up) {
		return this;
	}
	
	@Override
	public Vec3i getVector(AbsFace up) {
		return vector;
	}
	
	@Override
	public Vec3 getFloatVector(AbsFace up) {
		return floatVector;
	}
	
	@Override
	public Vec3 getNormalized(AbsFace up) {
		return normalized;
	}

	public Vec3i getVector() {
		return vector;
	}
	
	public Vec3 getFloatVector() {
		return floatVector;
	}

	public Vec3 getNormalized() {
		return normalized;
	}

}
