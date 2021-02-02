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
 
package ru.windcorp.progressia.common.util;

import java.util.function.Consumer;

import glm.Glm;
import glm.mat._3.Mat3;
import glm.mat._4.Mat4;
import glm.vec._2.Vec2;
import glm.vec._2.d.Vec2d;
import glm.vec._2.i.Vec2i;
import glm.vec._3.Vec3;
import glm.vec._3.d.Vec3d;
import glm.vec._3.i.Vec3i;
import glm.vec._4.Vec4;
import glm.vec._4.d.Vec4d;
import glm.vec._4.i.Vec4i;

public class VectorUtil {

	public static enum Axis {
		X, Y, Z, W;
	}
	
	public static enum SignedAxis {
		POS_X(Axis.X, +1),
		NEG_X(Axis.X, -1),
		POS_Y(Axis.Y, +1),
		NEG_Y(Axis.Y, -1),
		POS_Z(Axis.Z, +1),
		NEG_Z(Axis.Z, -1),
		POS_W(Axis.W, +1),
		NEG_W(Axis.W, -1);
		
		private final Axis axis;
		private final boolean isPositive;
		
		private SignedAxis(Axis axis, int sign) {
			this.axis = axis;
			this.isPositive = (sign == +1 ? true : false);
		}
		
		/**
		 * @return the axis
		 */
		public Axis getAxis() {
			return axis;
		}
		
		public boolean isPositive() {
			return isPositive;
		}
		
		public int getSign() {
			return isPositive ? +1 : -1;
		}
	}

	public static void iterateCuboid(
		int x0,
		int y0,
		int z0,
		int x1,
		int y1,
		int z1,
		Consumer<? super Vec3i> action
	) {
		Vec3i cursor = Vectors.grab3i();

		for (int x = x0; x < x1; ++x) {
			for (int y = y0; y < y1; ++y) {
				for (int z = z0; z < z1; ++z) {
					cursor.set(x, y, z);
					action.accept(cursor);
				}
			}
		}

		Vectors.release(cursor);
	}

	public static void iterateCuboid(
		Vec3i vMin,
		Vec3i vMax,
		Consumer<? super Vec3i> action
	) {
		iterateCuboid(vMin.x, vMin.y, vMin.z, vMax.x, vMax.y, vMax.z, action);
	}

	public static void iterateCuboidAround(
		int cx,
		int cy,
		int cz,
		int dx,
		int dy,
		int dz,
		Consumer<? super Vec3i> action
	) {
		if (dx < 0)
			throw new IllegalArgumentException("dx " + dx + " is negative");
		if (dy < 0)
			throw new IllegalArgumentException("dy " + dx + " is negative");
		if (dz < 0)
			throw new IllegalArgumentException("dz " + dx + " is negative");

		if (dx % 2 == 0)
			throw new IllegalArgumentException("dx " + dx + " is even, only odd accepted");
		if (dy % 2 == 0)
			throw new IllegalArgumentException("dy " + dy + " is even, only odd accepted");
		if (dz % 2 == 0)
			throw new IllegalArgumentException("dz " + dz + " is even, only odd accepted");

		dx /= 2;
		dy /= 2;
		dz /= 2;

		iterateCuboid(cx - dx, cy - dy, cz - dz, cx + dx + 1, cy + dy + 1, cz + dz + 1, action);
	}

	public static void iterateCuboidAround(
		Vec3i center,
		Vec3i diameters,
		Consumer<? super Vec3i> action
	) {
		iterateCuboidAround(center.x, center.y, center.z, diameters.x, diameters.y, diameters.z, action);
	}

	public static void iterateCuboidAround(
		int cx,
		int cy,
		int cz,
		int diameter,
		Consumer<? super Vec3i> action
	) {
		iterateCuboidAround(cx, cy, cz, diameter, diameter, diameter, action);
	}

	public static void iterateCuboidAround(
		Vec3i center,
		int diameter,
		Consumer<? super Vec3i> action
	) {
		iterateCuboidAround(center.x, center.y, center.z, diameter, action);
	}

	public static Vec3 applyMat4(Vec3 in, Mat4 mat, Vec3 out) {
		if (out == null) {
			out = new Vec3();
		}
		
		Vec4 vec4 = Vectors.grab4();
		vec4.set(in, 1f);

		mat.mul(vec4);

		out.set(vec4.x, vec4.y, vec4.z);
		Vectors.release(vec4);
		
		return out;
	}

	public static Vec3 applyMat4(Vec3 inOut, Mat4 mat) {
		return applyMat4(inOut, mat, inOut);
	}
	
	public static Vec3 rotate(Vec3 in, Vec3 axis, float angle, Vec3 out) {
		if (out == null) {
			out = new Vec3();
		}
		
		Mat3 mat = Matrices.grab3();
		
		mat.identity().rotate(angle, axis);
		mat.mul(in, out);
		
		Matrices.release(mat);
		
		return out;
	}
	
	public static Vec3 rotate(Vec3 inOut, Vec3 axis, float angle) {
		return rotate(inOut, axis, angle, inOut);
	}
	
	public static double getAngle(Vec3 from, Vec3 to, Vec3 normal) {
		Vec3 left = Vectors.grab3();
		
		left.set(normal).cross(from);
		double sign = Math.signum(left.dot(to));
		
		double result = (float) Math.acos(Glm.clamp(from.dot(to), -1, +1)) * sign;
		
		Vectors.release(left);
		return result;
	}
	
	public static Vec3 projectOnSurface(Vec3 in, Vec3 normal, Vec3 out) {
		if (in == out) {
			return projectOnSurface(in, normal);
		}
		
		if (out == null) {
			out = new Vec3();
		}
		
		out.set(normal).mul(-normal.dot(in)).add(in);
		
		return out;
	}
	
	public static Vec3 projectOnSurface(Vec3 inOut, Vec3 normal) {
		Vec3 buffer = Vectors.grab3();
		
		projectOnSurface(inOut, normal, buffer);
		inOut.set(buffer);
		
		Vectors.release(buffer);
		
		return inOut;
	}
	
	public static Vec3 projectOnVector(Vec3 in, Vec3 vector, Vec3 out) {
		if (out == null) {
			out = new Vec3();
		}
		
		float dot = vector.dot(in);
		out.set(vector).mul(dot);
		
		return out;
	}
	
	public static Vec3 projectOnVector(Vec3 inOut, Vec3 vector) {
		return projectOnVector(inOut, vector);
	}

	public static Vec3 linearCombination(
		Vec3 va,
		float ka,
		Vec3 vb,
		float kb,
		Vec3 output
	) {
		if (output == null) {
			output = new Vec3();
		}
		
		output.set(
			va.x * ka + vb.x * kb,
			va.y * ka + vb.y * kb,
			va.z * ka + vb.z * kb
		);
		return output;
	}

	public static Vec3 linearCombination(
		Vec3 va,
		float ka,
		Vec3 vb,
		float kb,
		Vec3 vc,
		float kc,
		Vec3 output
	) {
		if (output == null) {
			output = new Vec3();
		}
		
		output.set(
			va.x * ka + vb.x * kb + vc.x * kc,
			va.y * ka + vb.y * kb + vc.y * kc,
			va.z * ka + vb.z * kb + vc.z * kc
		);
		return output;
	}

	public static float get(Vec2 v, Axis a) {
		switch (a) {
		case X:
			return v.x;
		case Y:
			return v.y;
		default:
			throw new IllegalArgumentException("Vec2 does not have axis " + a);
		}
	}

	public static Vec2 set(Vec2 v, Axis a, float value) {
		switch (a) {
		case X:
			v.x = value;
			break;
		case Y:
			v.y = value;
			break;
		default:
			throw new IllegalArgumentException("Vec2 does not have axis " + a);
		}
		return v;
	}

	public static int get(Vec2i v, Axis a) {
		switch (a) {
		case X:
			return v.x;
		case Y:
			return v.y;
		default:
			throw new IllegalArgumentException("Vec2i does not have axis " + a);
		}
	}

	public static Vec2i set(Vec2i v, Axis a, int value) {
		switch (a) {
		case X:
			v.x = value;
			break;
		case Y:
			v.y = value;
			break;
		default:
			throw new IllegalArgumentException("Vec2i does not have axis " + a);
		}
		return v;
	}

	public static double get(Vec2d v, Axis a) {
		switch (a) {
		case X:
			return v.x;
		case Y:
			return v.y;
		default:
			throw new IllegalArgumentException("Vec2d does not have axis " + a);
		}
	}

	public static Vec2d set(Vec2d v, Axis a, double value) {
		switch (a) {
		case X:
			v.x = value;
			break;
		case Y:
			v.y = value;
			break;
		default:
			throw new IllegalArgumentException("Vec2d does not have axis " + a);
		}
		return v;
	}

	public static float get(Vec3 v, Axis a) {
		switch (a) {
		case X:
			return v.x;
		case Y:
			return v.y;
		case Z:
			return v.z;
		default:
			throw new IllegalArgumentException("Vec3 does not have axis " + a);
		}
	}

	public static Vec3 set(Vec3 v, Axis a, float value) {
		switch (a) {
		case X:
			v.x = value;
			break;
		case Y:
			v.y = value;
			break;
		case Z:
			v.z = value;
			break;
		default:
			throw new IllegalArgumentException("Vec3 does not have axis " + a);
		}
		return v;
	}

	public static int get(Vec3i v, Axis a) {
		switch (a) {
		case X:
			return v.x;
		case Y:
			return v.y;
		case Z:
			return v.z;
		default:
			throw new IllegalArgumentException("Vec3i does not have axis " + a);
		}
	}

	public static Vec3i set(Vec3i v, Axis a, int value) {
		switch (a) {
		case X:
			v.x = value;
			break;
		case Y:
			v.y = value;
			break;
		case Z:
			v.z = value;
			break;
		default:
			throw new IllegalArgumentException("Vec3i does not have axis " + a);
		}
		return v;
	}

	public static double get(Vec3d v, Axis a) {
		switch (a) {
		case X:
			return v.x;
		case Y:
			return v.y;
		case Z:
			return v.z;
		default:
			throw new IllegalArgumentException("Vec3d does not have axis " + a);
		}
	}

	public static Vec3d set(Vec3d v, Axis a, double value) {
		switch (a) {
		case X:
			v.x = value;
			break;
		case Y:
			v.y = value;
			break;
		case Z:
			v.z = value;
			break;
		default:
			throw new IllegalArgumentException("Vec3d does not have axis " + a);
		}
		return v;
	}

	public static float get(Vec4 v, Axis a) {
		switch (a) {
		case X:
			return v.x;
		case Y:
			return v.y;
		case Z:
			return v.z;
		case W:
			return v.w;
		default:
			throw new IllegalArgumentException("Vec4 does not have axis " + a);
		}
	}

	public static Vec4 set(Vec4 v, Axis a, float value) {
		switch (a) {
		case X:
			v.x = value;
			break;
		case Y:
			v.y = value;
			break;
		case Z:
			v.z = value;
			break;
		case W:
			v.w = value;
			break;
		default:
			throw new IllegalArgumentException("Vec4 does not have axis " + a);
		}
		return v;
	}

	public static int get(Vec4i v, Axis a) {
		switch (a) {
		case X:
			return v.x;
		case Y:
			return v.y;
		case Z:
			return v.z;
		case W:
			return v.w;
		default:
			throw new IllegalArgumentException("Vec4i does not have axis " + a);
		}
	}

	public static Vec4i set(Vec4i v, Axis a, int value) {
		switch (a) {
		case X:
			v.x = value;
			break;
		case Y:
			v.y = value;
			break;
		case Z:
			v.z = value;
			break;
		case W:
			v.w = value;
			break;
		default:
			throw new IllegalArgumentException("Vec4i does not have axis " + a);
		}
		return v;
	}

	public static double get(Vec4d v, Axis a) {
		switch (a) {
		case X:
			return v.x;
		case Y:
			return v.y;
		case Z:
			return v.z;
		case W:
			return v.w;
		default:
			throw new IllegalArgumentException("Vec4d does not have axis " + a);
		}
	}

	public static Vec4d set(Vec4d v, Axis a, double value) {
		switch (a) {
		case X:
			v.x = value;
			break;
		case Y:
			v.y = value;
			break;
		case Z:
			v.z = value;
			break;
		case W:
			v.w = value;
			break;
		default:
			throw new IllegalArgumentException("Vec4d does not have axis " + a);
		}
		return v;
	}

	private VectorUtil() {
	}

}
