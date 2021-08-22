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

import static ru.windcorp.progressia.common.util.VectorUtil.SignedAxis.NEG_X;
import static ru.windcorp.progressia.common.util.VectorUtil.SignedAxis.NEG_Y;
import static ru.windcorp.progressia.common.util.VectorUtil.SignedAxis.NEG_Z;
import static ru.windcorp.progressia.common.util.VectorUtil.SignedAxis.POS_X;
import static ru.windcorp.progressia.common.util.VectorUtil.SignedAxis.POS_Y;
import static ru.windcorp.progressia.common.util.VectorUtil.SignedAxis.POS_Z;

import java.util.Map;

import glm.mat._3.Mat3;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.VectorUtil.SignedAxis;

public class AxisRotations {
	
	private static class Rotation {
		private static class MyMat3i {
			private final int m00, m01, m02, m10, m11, m12, m20, m21, m22;

			public MyMat3i(Mat3 integerMatrix) {
				this.m00 = (int) integerMatrix.m00;
				this.m01 = (int) integerMatrix.m01;
				this.m02 = (int) integerMatrix.m02;
				this.m10 = (int) integerMatrix.m10;
				this.m11 = (int) integerMatrix.m11;
				this.m12 = (int) integerMatrix.m12;
				this.m20 = (int) integerMatrix.m20;
				this.m21 = (int) integerMatrix.m21;
				this.m22 = (int) integerMatrix.m22;
			}
			
			public Vec3i mul(Vec3i right, Vec3i res) {
				res.set(
					m00 * right.x + m10 * right.y + m20 * right.z,
					m01 * right.x + m11 * right.y + m21 * right.z,
					m02 * right.x + m12 * right.y + m22 * right.z
				);
				return res;
			}
		}
		
		private final Mat3 resolutionMatrix3 = new Mat3();
		private final Mat4 resolutionMatrix4 = new Mat4();
		private final MyMat3i resolutionMatrix3i;
		
		private final Mat3 relativizationMatrix3 = new Mat3();
		private final Mat4 relativizationMatrix4 = new Mat4();
		private final MyMat3i relativizationMatrix3i;
		
		private Rotation(SignedAxis northDestination, SignedAxis westDestination, SignedAxis upDestination) {
			resolutionMatrix3.c0(computeUnitVectorAlong(northDestination));
			resolutionMatrix3.c1(computeUnitVectorAlong(westDestination));
			resolutionMatrix3.c2(computeUnitVectorAlong(upDestination));
			
			resolutionMatrix3.toMat4(resolutionMatrix4);
			resolutionMatrix3i = new MyMat3i(resolutionMatrix3);
			
			relativizationMatrix3.set(resolutionMatrix3).transpose();
			relativizationMatrix4.set(resolutionMatrix4).transpose();
			relativizationMatrix3i = new MyMat3i(relativizationMatrix3);
		}

		private static Vec3 computeUnitVectorAlong(SignedAxis signedAxis) {
			Vec3 result = new Vec3(0, 0, 0);
			VectorUtil.set(result, signedAxis.getAxis(), signedAxis.getSign());
			return result;
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
		
		public Vec3i resolve(Vec3i output, Vec3i input) {
			if (output == null) {
				output = new Vec3i();
			}
			resolutionMatrix3i.mul(input, output);
			return output;
		}
		
		public Vec3i relativize(Vec3i output, Vec3i input) {
			if (output == null) {
				output = new Vec3i();
			}
			relativizationMatrix3i.mul(input, output);
			return output;
		}
		
		public Vec3 resolve(Vec3 output, Vec3 input) {
			if (output == null) {
				output = new Vec3();
			}
			resolutionMatrix3.mul(input, output);
			return output;
		}
		
		public Vec3 relativize(Vec3 output, Vec3 input) {
			if (output == null) {
				output = new Vec3();
			}
			relativizationMatrix3.mul(input, output);
			return output;
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
	
	public static Vec3i resolve(Vec3i relative, AbsFace up, Vec3i output) {
		return TRANSFORMATIONS.get(up).resolve(output, relative);
	}
	
	public static Vec3 resolve(Vec3 relative, AbsFace up, Vec3 output) {
		return TRANSFORMATIONS.get(up).resolve(output, relative);
	}
	
	public static Vec3i relativize(Vec3i absolute, AbsFace up, Vec3i output) {
		return TRANSFORMATIONS.get(up).relativize(output, absolute);
	}
	
	public static Vec3 relativize(Vec3 absolute, AbsFace up, Vec3 output) {
		return TRANSFORMATIONS.get(up).relativize(output, absolute);
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

}
