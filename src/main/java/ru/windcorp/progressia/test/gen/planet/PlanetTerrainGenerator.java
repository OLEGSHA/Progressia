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

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.ArrayFloatRangeMap;
import ru.windcorp.progressia.common.util.FloatRangeMap;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.test.gen.TerrainLayer;
import ru.windcorp.progressia.test.gen.surface.SurfaceFloatField;
import ru.windcorp.progressia.test.gen.surface.SurfaceTerrainGenerator;

class PlanetTerrainGenerator {

	private final TestPlanetGenerator parent;
	private final SurfaceTerrainGenerator surfaceGenerator;

	public PlanetTerrainGenerator(TestPlanetGenerator generator) {
		this.parent = generator;

		SurfaceFloatField heightMap = new TestHeightMap(
			generator.getPlanet().getRadius() - ChunkData.BLOCKS_PER_CHUNK,
			generator.getPlanet().getRadius() / 4,
			5,
			6
		);

		FloatRangeMap<TerrainLayer> layers = new ArrayFloatRangeMap<>();
		BlockData granite = BlockDataRegistry.getInstance().get("Test:GraniteMonolith");
		BlockData dirt = BlockDataRegistry.getInstance().get("Test:Dirt");
		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");
		layers.put(Float.NEGATIVE_INFINITY, 0, (n, w, d, r, c) -> air);
		layers.put(0, 4, (n, w, d, r, c) -> dirt);
		layers.put(4, Float.POSITIVE_INFINITY, (n, w, d, r, c) -> granite);

		this.surfaceGenerator = new SurfaceTerrainGenerator((f, n, w) -> heightMap.get(f, n, w) + generator.getPlanet().getRadius(), layers);
	}

	public TestPlanetGenerator getGenerator() {
		return parent;
	}

	public ChunkData generateTerrain(Vec3i chunkPos) {
		ChunkData chunk = new ChunkData(chunkPos, getGenerator().getWorldData());

		if (isOrdinaryChunk(chunkPos)) {
			generateOrdinaryTerrain(chunk);
		} else {
			generateBorderTerrain(chunk);
		}

		return chunk;
	}

	private boolean isOrdinaryChunk(Vec3i chunkPos) {
		Vec3i sorted = VectorUtil.sortAfterAbs(chunkPos, null);
		return sorted.x != sorted.y;
	}

	private void generateOrdinaryTerrain(ChunkData chunk) {
		surfaceGenerator.generateTerrain(chunk);
	}

	private void generateBorderTerrain(ChunkData chunk) {
		BlockData stone = BlockDataRegistry.getInstance().get("Test:Stone");
		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");
		
		float radius = parent.getPlanet().getRadius();

		Vec3 biw = new Vec3();
		
		chunk.forEachBiC(bic -> {
			
			biw.set(
				Coordinates.getInWorld(chunk.getX(), bic.x),
				Coordinates.getInWorld(chunk.getY(), bic.y),
				Coordinates.getInWorld(chunk.getZ(), bic.z)
			);
			
			biw.sub(ChunkData.CHUNK_RADIUS - 0.5f);
			VectorUtil.sortAfterAbs(biw, biw);
			
			chunk.setBlock(bic, biw.x <= radius ? stone : air, false);
			
		});
	}

}
