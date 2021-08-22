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
package ru.windcorp.progressia.server.world.generation.planet;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.GravityModel;
import ru.windcorp.progressia.common.world.rels.AbsFace;

import static java.lang.Math.*;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class PlanetGravityModel extends GravityModel {
	
	public static class Settings {
		public float surfaceGravitationalAcceleration;
		public float curvature;
		public float innerRadius;
		
		public Settings() {}
		
		public Settings(float surfaceGravitationalAcceleration, float curvature, float innerRadius) {
			this.surfaceGravitationalAcceleration = surfaceGravitationalAcceleration;
			this.curvature = curvature;
			this.innerRadius = innerRadius;
		}
		
		public void copyFrom(Settings copyFrom) {
			this.surfaceGravitationalAcceleration = copyFrom.surfaceGravitationalAcceleration;
			this.curvature = copyFrom.curvature;
			this.innerRadius = copyFrom.innerRadius;
		}

		public void read(DataInput input) throws IOException, DecodingException {
			surfaceGravitationalAcceleration = input.readFloat();
			curvature = input.readFloat();
			innerRadius = input.readFloat();
		}
		
		public void write(DataOutput output) throws IOException {
			output.writeFloat(surfaceGravitationalAcceleration);
			output.writeFloat(curvature);
			output.writeFloat(innerRadius);
		}
	}
	
	private Settings settings = new Settings();

	public PlanetGravityModel(String id) {
		super(id);
	}

	public float getSurfaceGravitationalAcceleration() {
		return settings.surfaceGravitationalAcceleration;
	}

	public float getCurvature() {
		return settings.curvature;
	}

	public float getInnerRadius() {
		return settings.innerRadius;
	}
	
	public void configure(Settings settings) {
		this.settings = settings;
	}

	@Override
	protected void doGetGravity(Vec3 pos, Vec3 output) {
		float r = getInnerRadius();
		float c = getCurvature();
		
		// Change to a CS where (0;0;0) is the center of the center chunk
		float px = pos.x - DefaultChunkData.CHUNK_RADIUS + 0.5f;
		float py = pos.y - DefaultChunkData.CHUNK_RADIUS + 0.5f;
		float pz = pos.z - DefaultChunkData.CHUNK_RADIUS + 0.5f;
		
		// Assume weightlessness when too close to center
		if ((px*px + py*py + pz*pz) < r*r) {
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
		
		output.x = maxAbs - ax < c ? (px > 0 ? +1 : -1) : 0;
		output.y = maxAbs - ay < c ? (py > 0 ? +1 : -1) : 0;
		output.z = maxAbs - az < c ? (pz > 0 ? +1 : -1) : 0;
		
		if (maxAbs - midAbs < c) {
			output.normalize();
			computeEdgeGravity(output.x, output.y, output.z, px, py, pz, output);
		} else {
			assert output.dot(output) == 1 : "maxAbs - midAbs = " + maxAbs + " - " + midAbs + " > " + c + " yet l*l != 1";
		}
		
		output.mul(-getSurfaceGravitationalAcceleration());
	}

	private void computeEdgeGravity(float lx, float ly, float lz, float rx, float ry, float rz, Vec3 output) {
		// da math is gud, no worry
		//  - Javapony
		
		float c = getCurvature();
		
		if (lx == 0) rx = 0;
		if (ly == 0) ry = 0;
		if (lz == 0) rz = 0;
		
		float scalarProduct = rx*lx + ry*ly + rz*lz;
		float rSquared = rx*rx + ry*ry + rz*rz;
		
		float distanceAlongEdge = scalarProduct - (float) sqrt(
			scalarProduct*scalarProduct - rSquared + c*c
		);
		
		output.set(lx, ly, lz).mul(-distanceAlongEdge).add(rx, ry, rz).div(c);
		
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
	
	@Override
	protected void doReadSettings(DataInput input) throws IOException, DecodingException {
		this.settings.read(input);
	}
	
	@Override
	protected void doWriteSettings(DataOutput output) throws IOException {
		this.settings.write(output);
	}

}
