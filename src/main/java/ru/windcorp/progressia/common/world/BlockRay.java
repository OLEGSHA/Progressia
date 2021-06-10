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

package ru.windcorp.progressia.common.world;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.VectorUtil.Axis;
import ru.windcorp.progressia.common.world.block.BlockFace;

import static java.lang.Math.*;

public class BlockRay {

	private final Vec3 position = new Vec3();
	private final Vec3 direction = new Vec3();

	private float distance;

	private final Vec3i block = new Vec3i();
	private BlockFace currentFace = null;

	private boolean isValid = false;

	public void start(Vec3 position, Vec3 direction) {
		if (!direction.any()) {
			throw new IllegalArgumentException("Direction is a zero vector");
		}

		isValid = true;
		this.position.set(position).sub(0.5f); // Make sure lattice points are
												// block vertices, not centers
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
		VectorUtil.linearCombination(position, 1, direction, tMin, position); // position
																				// +=
																				// direction
																				// *
																				// tMin
		distance += tMin;

		// position.(axis) = round(position.(axis))
		VectorUtil.set(position, axis, round(VectorUtil.get(position, axis)));

		this.currentFace = computeCurrentFace(axis, (int) signum(VectorUtil.get(direction, axis)));

		return block;
	}

	private static float distanceToEdge(float c, float dir) {
		if (dir == 0)
			return Float.POSITIVE_INFINITY;

		float edge;

		if (dir > 0) {
			edge = strictCeil(c);
		} else {
			edge = strictFloor(c);
		}

		return (edge - c) / dir;
	}

	private BlockFace computeCurrentFace(Axis axis, int sign) {
		if (sign == 0)
			throw new IllegalStateException("sign is zero");

		switch (axis) {
		case X:
			return sign > 0 ? BlockFace.SOUTH : BlockFace.NORTH;
		case Y:
			return sign > 0 ? BlockFace.EAST : BlockFace.WEST;
		default:
		case Z:
			return sign > 0 ? BlockFace.BOTTOM : BlockFace.TOP;
		}
	}

	public Vec3i current() {
		checkState();
		return block;
	}

	public Vec3 getPoint(Vec3 output) {
		output.set(position);
		output.add(0.5f); // Make sure we're in the block-center coordinate
							// system
		return output;
	}

	public BlockFace getCurrentFace() {
		return currentFace;
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
	 * 
	 * @param c
	 *            the number to compute strict ceiling of
	 * @return the strict ceiling of <i>c</i>
	 */
	private static float strictCeil(float c) {
		return (float) (floor(c) + 1);
	}

	/**
	 * Returns a largest integer <i>a</i> such that <i>a</i> < <i>c</i>.
	 * 
	 * @param c
	 *            the number to compute strict ceiling of
	 * @return the strict ceiling of <i>c</i>
	 */
	private static float strictFloor(float c) {
		return (float) (ceil(c) - 1);
	}

}
