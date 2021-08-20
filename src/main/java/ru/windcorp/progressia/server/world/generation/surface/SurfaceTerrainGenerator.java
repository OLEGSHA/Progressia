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
package ru.windcorp.progressia.server.world.generation.surface;

import java.util.Random;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.CoordinatePacker;
import ru.windcorp.progressia.common.util.FloatRangeMap;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.rels.AxisRotations;

public class SurfaceTerrainGenerator {
	
	private final SurfaceFloatField heightMap;
	private final FloatRangeMap<TerrainLayer> layers;

	public SurfaceTerrainGenerator(SurfaceFloatField heightMap, FloatRangeMap<TerrainLayer> layers) {
		this.heightMap = heightMap;
		this.layers = layers;
	}
	
	public void generateTerrain(DefaultChunkData chunk) {
		
		Vec3i relBIC = new Vec3i();
		
		Vec3 offset = new Vec3(chunk.getMinX(), chunk.getMinY(), chunk.getMinZ());
		AxisRotations.relativize(offset, chunk.getUp(), offset);
		offset.z -= DefaultChunkData.CHUNK_RADIUS - 0.5f;
		
		Random random = new Random(CoordinatePacker.pack3IntsIntoLong(chunk.getPosition()) /* ^ seed*/);
		
		for (relBIC.x = 0; relBIC.x < DefaultChunkData.BLOCKS_PER_CHUNK; ++relBIC.x) {
			for (relBIC.y = 0; relBIC.y < DefaultChunkData.BLOCKS_PER_CHUNK; ++relBIC.y) {
				generateColumn(chunk, relBIC, offset, random);
			}
		}
		
	}
	
	public void generateColumn(DefaultChunkData chunk, Vec3i relBIC, Vec3 offset, Random random) {
		
		float north = relBIC.x + offset.x;
		float west = relBIC.y + offset.y;
		
		float relSurface = heightMap.get(chunk.getUp(), north, west) - offset.z;
		
		for (relBIC.z = 0; relBIC.z < DefaultChunkData.BLOCKS_PER_CHUNK; ++relBIC.z) {
			float depth = relSurface - relBIC.z;
			BlockData block = layers.get(depth).get(chunk.getUp(), north, west, depth, random);
			
			chunk.resolve(relBIC, relBIC);
			chunk.setBlock(relBIC, block, false);
			chunk.relativize(relBIC, relBIC);
		}
		
	}

}
