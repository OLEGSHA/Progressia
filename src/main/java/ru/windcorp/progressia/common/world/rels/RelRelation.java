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

import java.util.Map;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.VectorUtil.SignedAxis;

import static ru.windcorp.progressia.common.util.VectorUtil.SignedAxis.*;

/**
 * Name stands for Relative Relation 
 */
public class RelRelation extends BlockRelation {
	
	private static class Rotation {
		private final SignedAxis northDestination;
		private final SignedAxis westDestination;
		private final SignedAxis upDestination;
		
		public Rotation(SignedAxis northDestination, SignedAxis westDestination, SignedAxis upDestination) {
			this.northDestination = northDestination;
			this.westDestination = westDestination;
			this.upDestination = upDestination;
		}
		
		public Vec3i apply(Vec3i output, Vec3i input) {
			if (output == null) {
				return output;
			}
			
			set(output, input.x, northDestination);
			set(output, input.y, westDestination);
			set(output, input.z, upDestination);
			
			return output;
		}
		
		private static void set(Vec3i output, int value, SignedAxis axis) {
			VectorUtil.set(output, axis.getAxis(), axis.isPositive() ? +value : -value);
		}
	}
	
	private final static Map<AbsFace, Rotation> TRANSFORMATIONS = AbsFace.mapToFaces(
		new Rotation(POS_X, POS_Y, POS_Z),
		new Rotation(POS_X, NEG_Y, NEG_Z),
		new Rotation(POS_Z, NEG_Y, POS_X),
		new Rotation(POS_Z, POS_Y, NEG_X),
		new Rotation(POS_Z, NEG_X, NEG_Y),
		new Rotation(POS_Z, POS_X, POS_Y)
	);
	
	private final Vec3i vector = new Vec3i();
	private AbsRelation[] resolved = null;
	
	public RelRelation(int north, int west, int up) {
		this(north, west, up, false);
	}
	
	protected RelRelation(int north, int west, int up, boolean precomputeAllResolutions) {
		vector.set(north, west, up);
		
		if (precomputeAllResolutions) {
			for (AbsFace face : AbsFace.getFaces()) {
				resolve(face);
			}
		}
	}
	
	/**
	 * @return the relative vector (northward, westward, upward)
	 */
	public Vec3i getVector() {
		return vector;
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
		return new AbsRelation(TRANSFORMATIONS.get(up).apply(new Vec3i(), vector));
	}

	@Override
	protected Vec3i getSample() {
		return getVector();
	}

}
