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
package ru.windcorp.progressia.test.gen;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.GravityModel;
import ru.windcorp.progressia.common.world.rels.AbsFace;

import static java.lang.Math.*;

public class TestPlanetGravityModel extends GravityModel {
	
	private static final float GRAVITATIONAL_ACCELERATION = Units.get("9.8 m/s^2");
	private static final float ROUNDNESS = Units.get("16 m");
	private static final float INNER_RADIUS = Units.get("16 m");
	
	public TestPlanetGravityModel() {
		this("Test:PlanetGravityModel");
	}

	protected TestPlanetGravityModel(String id) {
		super(id);
	}

	@Override
	protected void doGetGravity(Vec3 pos, Vec3 output) {
		// Change to a CS where (0;0;0) is the center of the center chunk
		float px = pos.x - ChunkData.CHUNK_RADIUS + 0.5f;
		float py = pos.y - ChunkData.CHUNK_RADIUS + 0.5f;
		float pz = pos.z - ChunkData.CHUNK_RADIUS + 0.5f;
		
		// Assume weightlessness when too close to center
		if ((px*px + py*py + pz*pz) < INNER_RADIUS*INNER_RADIUS) {
			output.set(0, 0, 0);
			return;
		}
		
		// Cache absolute coordinates
		float ax = abs(px);
		float ay = abs(py);
		float az = abs(pz);
		
		// Determine maximum and middle coordinates by absolute value
		final float maxAbs;
		final float midAbs;
		
		// herptyderp
		if (ax > ay) {
			if (ax > az) {
				maxAbs = ax;
				midAbs = ay > az ? ay : az;
			} else {
				maxAbs = az;
				midAbs = ax;
			}
		} else {
			if (ay > az) {
				maxAbs = ay;
				midAbs = ax > az ? ax : az;
			} else {
				maxAbs = az;
				midAbs = ay;
			}
		}
		
		output.x = maxAbs - ax < ROUNDNESS ? (px > 0 ? +1 : -1) : 0;
		output.y = maxAbs - ay < ROUNDNESS ? (py > 0 ? +1 : -1) : 0;
		output.z = maxAbs - az < ROUNDNESS ? (pz > 0 ? +1 : -1) : 0;
		
		if (maxAbs - midAbs < ROUNDNESS) {
			output.normalize();
			computeEdgeGravity(output.x, output.y, output.z, px, py, pz, output);
		} else {
			assert output.dot(output) == 1 : "maxAbs - midAbs = " + maxAbs + " - " + midAbs + " > " + ROUNDNESS + " yet l*l != 1";
		}
		
		output.mul(-GRAVITATIONAL_ACCELERATION);
	}

	private void computeEdgeGravity(float lx, float ly, float lz, float rx, float ry, float rz, Vec3 output) {
		// da math is gud, no worry
		//  - Javapony
		
		if (lx == 0) rx = 0;
		if (ly == 0) ry = 0;
		if (lz == 0) rz = 0;
		
		float scalarProduct = rx*lx + ry*ly + rz*lz;
		float rSquared = rx*rx + ry*ry + rz*rz;
		
		float distanceAlongEdge = scalarProduct - (float) sqrt(
			scalarProduct*scalarProduct - rSquared + ROUNDNESS*ROUNDNESS
		);
		
		output.set(lx, ly, lz).mul(-distanceAlongEdge).add(rx, ry, rz).div(ROUNDNESS);
		
		final float f = (float) sqrt(3.0/2);
		
		if (signum(lx) != signum(output.x)) {
			computeEdgeGravity(0, ly*f, lz*f, rx, ry, rz, output);
		} else if (signum(ly) != signum(output.y)) {
			computeEdgeGravity(lx*f, 0, lz*f, rx, ry, rz, output);
		} else if (signum(lz) != signum(output.z)) {
			computeEdgeGravity(lx*f, ly*f, 0, rx, ry, rz, output);
		}
	}

	@Override
	protected AbsFace doGetDiscreteUp(Vec3i chunkPos) {
		AbsFace rounded = AbsFace.roundToFace(chunkPos.x, chunkPos.y, chunkPos.z);
		return rounded == null ? AbsFace.POS_Z : rounded;
	}

}
