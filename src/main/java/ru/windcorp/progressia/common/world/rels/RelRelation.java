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

import glm.mat._3.Mat3;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
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
		
		private final Mat3 resolutionMatrix3 = new Mat3();
		private final Mat4 resolutionMatrix4 = new Mat4();
		
		private final Mat3 relativizationMatrix3 = new Mat3();
		private final Mat4 relativizationMatrix4 = new Mat4();
		
		private Rotation(SignedAxis northDestination, SignedAxis westDestination, SignedAxis upDestination) {
			this.northDestination = northDestination;
			this.westDestination = westDestination;
			this.upDestination = upDestination;
			
			resolutionMatrix3.c0(apply(null, new Vec3(1, 0, 0)));
			resolutionMatrix3.c1(apply(null, new Vec3(0, 1, 0)));
			resolutionMatrix3.c2(apply(null, new Vec3(0, 0, 1)));
			resolutionMatrix3.toMat4(resolutionMatrix4);
			
			relativizationMatrix3.set(resolutionMatrix3).transpose();
			relativizationMatrix4.set(resolutionMatrix4).transpose();
		}
		
		/**
		 * @return the resolutionMatrix3
		 */
		public Mat3 getResolutionMatrix3() {
			return resolutionMatrix3;
		}
		
		/**
		 * @return the resolutionMatrix4
		 */
		public Mat4 getResolutionMatrix4() {
			return resolutionMatrix4;
		}
		
		/**
		 * @return the relativizationMatrix3
		 */
		public Mat3 getRelativizationMatrix3() {
			return relativizationMatrix3;
		}
		
		/**
		 * @return the relativizationMatrix4
		 */
		public Mat4 getRelativizationMatrix4() {
			return relativizationMatrix4;
		}
		
		public Vec3i apply(Vec3i output, Vec3i input) {
			if (output == null) {
				output = new Vec3i();
			}
			
			int inX = input.x, inY = input.y, inZ = input.z;
			
			set(output, inX, northDestination);
			set(output, inY, westDestination);
			set(output, inZ, upDestination);
			
			return output;
		}
		
		private static void set(Vec3i output, int value, SignedAxis axis) {
			VectorUtil.set(output, axis.getAxis(), axis.isPositive() ? +value : -value);
		}
		
		public Vec3 apply(Vec3 output, Vec3 input) {
			if (output == null) {
				output = new Vec3();
			}
			
			float inX = input.x, inY = input.y, inZ = input.z;
			
			set(output, inX, northDestination);
			set(output, inY, westDestination);
			set(output, inZ, upDestination);
			
			return output;
		}
		
		private static void set(Vec3 output, float value, SignedAxis axis) {
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
		resolve(vector, up, resolution);
		AbsRelation result = AbsRelation.of(resolution);
		Vectors.release(resolution);
		return result;
	}
	
	public static Vec3i resolve(Vec3i relative, AbsFace up, Vec3i output) {
		if (output == null) {
			output = new Vec3i();
		}
		
		TRANSFORMATIONS.get(up).apply(output, relative);
		
		return output;
	}
	
	public static Mat3 getResolutionMatrix3(AbsFace up) {
		return TRANSFORMATIONS.get(up).getResolutionMatrix3();
	}
	
	public static Mat4 getResolutionMatrix4(AbsFace up) {
		return TRANSFORMATIONS.get(up).getResolutionMatrix4();
	}
	

	public static Mat3 getRelativizationMatrix3(AbsFace up) {
		return TRANSFORMATIONS.get(up).getRelativizationMatrix3();
	}
	
	public static Mat4 getRelativizationMatrix4(AbsFace up) {
		return TRANSFORMATIONS.get(up).getRelativizationMatrix4();
	}

	@Override
	protected Vec3i getSample() {
		return getRelVector();
	}

}
