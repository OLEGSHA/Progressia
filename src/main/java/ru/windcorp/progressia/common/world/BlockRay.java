package ru.windcorp.progressia.common.world;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.VectorUtil.Axis;

import static java.lang.Math.*;

public class BlockRay {
	
	private final Vec3 position = new Vec3();
	private final Vec3 direction = new Vec3();
	
	private float distance;
	
	private final Vec3i block = new Vec3i();
	
	private boolean isValid = false;
	
	public void start(Vec3 position, Vec3 direction) {
		if (!direction.any()) {
			throw new IllegalArgumentException("Direction is a zero vector");
		}

		isValid = true;
		this.position.set(position).sub(0.5f); // Make sure lattice points are block vertices, not centers
		this.direction.set(direction).normalize();
		this.block.set(toBlock(position.x), toBlock(position.y), toBlock(position.z));
		this.distance = 0;
	}

	public void end() {
		isValid = false;
	}
	
	public Vec3i next() {
		checkState();
		
		float tx = distanceToEdge(position.x, direction.x);
		float ty = distanceToEdge(position.y, direction.y);
		float tz = distanceToEdge(position.z, direction.z);
		
		float tMin;
		Axis axis;
		
		if (tx < ty && tx < tz) {
			tMin = tx;
			axis = Axis.X;
		} else if (ty < tx && ty < tz) {
			tMin = ty;
			axis = Axis.Y;
		} else {
			tMin = tz;
			axis = Axis.Z;
		}
		
		// block.(axis) += signum(direction.(axis))
		VectorUtil.set(block, axis, VectorUtil.get(block, axis) + (int) signum(VectorUtil.get(direction, axis)));
		
		// position += direction * tMin
		VectorUtil.linearCombination(position, 1, direction, tMin, position); // position += direction * tMin
		distance += tMin;
		
		// position.(axis) = round(position.(axis))
		VectorUtil.set(position, axis, round(VectorUtil.get(position, axis)));
		
		return block;
	}
	
	public Vec3i current() {
		checkState();
		return block;
	}

	private static float distanceToEdge(float c, float dir) {
		if (dir == 0) return Float.POSITIVE_INFINITY;
		
		float edge;
		
		if (dir > 0) {
			edge = strictCeil(c);
		} else {
			edge = strictFloor(c);
		}
		
		return (edge - c) / dir;
	}

	public float getDistance() {
		checkState();
		return distance;
	}
	
	private void checkState() {
		if (!isValid) {
			throw new IllegalStateException("BlockRay not started");
		}
	}
	
	private static int toBlock(float c) {
		return (int) round(c);
	}
	
	/**
	 * Returns a smallest integer <i>a</i> such that <i>a</i> > <i>c</i>.
	 * @param c the number to compute strict ceiling of
	 * @return the strict ceiling of <i>c</i>
	 */
	private static float strictCeil(float c) {
		return (float) (floor(c) + 1);
	}
	
	/**
	 * Returns a largest integer <i>a</i> such that <i>a</i> < <i>c</i>.
	 * @param c the number to compute strict ceiling of
	 * @return the strict ceiling of <i>c</i>
	 */
	private static float strictFloor(float c) {
		return (float) (ceil(c) - 1);
	}

}
