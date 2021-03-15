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
package ru.windcorp.progressia.test.gen.planet;

import ru.windcorp.progressia.common.world.ChunkData;

public class Planet {
	
	private final int radiusInChunks;
	
	private final float curvature;
	private final float surfaceGravitationalAcceleration;
	private final float innerGravityRadius;
	
	public Planet(
		int radiusInChunks,
		float curvature,
		float surfaceGravitationalAcceleration,
		float innerGravityRadius
	) {
		this.radiusInChunks = radiusInChunks;
		this.curvature = curvature;
		this.surfaceGravitationalAcceleration = surfaceGravitationalAcceleration;
		this.innerGravityRadius = innerGravityRadius;
	}
	
	/**
	 * @return the radiusInChunks
	 */
	public int getRadiusInChunks() {
		return radiusInChunks;
	}
	
	public float getRadius() {
		return radiusInChunks * ChunkData.BLOCKS_PER_CHUNK + ChunkData.CHUNK_RADIUS;
	}
	
	public int getDiameterInChunks() {
		return radiusInChunks * 2 + 1;
	}
	
	public float getDiameter() {
		return getDiameterInChunks() * ChunkData.BLOCKS_PER_CHUNK;
	}
	
	/**
	 * @return the curvature
	 */
	public float getCurvature() {
		return curvature;
	}
	
	/**
	 * @return the innerGravityRadius
	 */
	public float getInnerGravityRadius() {
		return innerGravityRadius;
	}
	
	/**
	 * @return the surfaceGravitationalAcceleration
	 */
	public float getSurfaceGravitationalAcceleration() {
		return surfaceGravitationalAcceleration;
	}

}
