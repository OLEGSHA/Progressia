package ru.windcorp.progressia.common.util;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;

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
	
	private VectorUtil() {}

}
