package ru.windcorp.progressia.common.util;

import java.util.function.Consumer;

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import glm.vec._4.Vec4;

public class VectorUtil {
	
	public static void forEachVectorInCuboid(
			int x0, int y0, int z0,
			int x1, int y1, int z1,
			Consumer<Vec3i> action
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
	
	public static void applyMat4(Vec3 in, Mat4 mat, Vec3 out) {
		Vec4 vec4 = Vectors.grab4();
		vec4.set(in, 1f);
		
		mat.mul(vec4);
		
		out.set(vec4.x, vec4.y, vec4.z);
		Vectors.release(vec4);
	}
	
	public static void applyMat4(Vec3 inOut, Mat4 mat) {
		Vec4 vec4 = Vectors.grab4();
		vec4.set(inOut, 1f);
		
		mat.mul(vec4);
		
		inOut.set(vec4.x, vec4.y, vec4.z);
	}
	
	private VectorUtil() {}

}
