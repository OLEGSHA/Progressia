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

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.FloatRangeMap;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.rels.AxisRotations;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceBlockContext;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceWorldContext;

public class SurfaceTerrainGenerator {

	private final Surface surface;

	private final SurfaceFloatField heightMap;
	private final FloatRangeMap<TerrainLayer> layers;

	public SurfaceTerrainGenerator(Surface surface, SurfaceFloatField heightMap, FloatRangeMap<TerrainLayer> layers) {
		this.surface = surface;
		this.heightMap = heightMap;
		this.layers = layers;
	}

	public void generateTerrain(Server server, DefaultChunkData chunk) {
		
		Vec3i relBIC = new Vec3i();
		
		Vec3 offset = new Vec3(chunk.getMinX(), chunk.getMinY(), chunk.getMinZ());
		AxisRotations.relativize(offset, chunk.getUp(), offset);
		
		SurfaceWorldContext context = surface.createContext(server, chunk, 0);
		
		for (relBIC.x = 0; relBIC.x < DefaultChunkData.BLOCKS_PER_CHUNK; ++relBIC.x) {
			for (relBIC.y = 0; relBIC.y < DefaultChunkData.BLOCKS_PER_CHUNK; ++relBIC.y) {
				generateColumn(chunk, relBIC, offset, context);
			}
		}
		
	}

	public void generateColumn(DefaultChunkData chunk, Vec3i relBIC, Vec3 offset, SurfaceWorldContext context) {

		int north = (int) (relBIC.x + offset.x);
		int west = (int) (relBIC.y + offset.y);

		float relSurface = heightMap.get(chunk.getUp(), north, west) - offset.z + DefaultChunkData.CHUNK_RADIUS - 0.5f;
		Vec3i location = Vectors.grab3i();

		for (relBIC.z = 0; relBIC.z < DefaultChunkData.BLOCKS_PER_CHUNK; ++relBIC.z) {
			float depth = relSurface - relBIC.z;
			int altitude = (int) (relBIC.z + offset.z);
			
			location.set(north, west, altitude);
			SurfaceBlockContext blockContext = context.push(location);
			
			BlockData block = layers.get(depth).get(blockContext, depth);
			
			blockContext.pop();

			chunk.resolve(relBIC, relBIC);
			chunk.setBlock(relBIC, block, false);
			chunk.relativize(relBIC, relBIC);
		}
		
		Vectors.release(location);

	}

}
