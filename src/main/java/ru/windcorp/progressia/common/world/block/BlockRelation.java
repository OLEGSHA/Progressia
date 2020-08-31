package ru.windcorp.progressia.common.world.block;

import static java.lang.Math.abs;
import static java.lang.Math.max;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;

public class BlockRelation {
	
	private final Vec3i vector = new Vec3i();
	private final Vec3 normalized = new Vec3();
	
	public BlockRelation(int x, int y, int z) {
		vector.set(x, y, z);
		normalized.set(x, y, z).normalize();
	}
	
	public BlockRelation(Vec3i vector) {
		this(vector.x, vector.y, vector.z);
	}
	
	public Vec3i getVector() {
		return vector;
	}
	
	public Vec3 getNormalized() {
		return normalized;
	}
	
	/**
	 * Returns the distance between the source and destination blocks, as
	 * defined by the Euclidean space. Your everyday distance.
	 * @return square root of the sum of the squares of the coordinates
	 */
	public float getEuclideanDistance() {
		return vector.length();
	}
	
	/**
	 * Returns the Manhattan distance, also known as the taxicab distance,
	 * between the source and the destination blocks. Manhattan distance is
	 * defined as the sum of the absolute values of the coordinates,
	 * which is also the minimum amount of block faces that need to be crossed
	 * to move from source to destination. 
	 * @return the sum of the absolute values of the coordinates
	 */
	public int getManhattanDistance() {
		return abs(vector.x) + abs(vector.y) + abs(vector.z);
	}
	
	/**
	 * Returns the Chebyshev distance between the source and the destination
	 * blocks. Chebyshev distance is defined as the maximum of the absolute
	 * values of the coordinates. 
	 * @return the maximum of the absolute values of the coordinates
	 */
	public int getChebyshevDistance() {
		return max(abs(vector.x), max(abs(vector.y), abs(vector.z)));
	}

}
