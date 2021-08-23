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

import java.util.Map;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.FloatRangeMap;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.generic.GenericChunks;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.generation.surface.Surface;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFloatField;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceTerrainGenerator;
import ru.windcorp.progressia.server.world.generation.surface.TerrainLayer;

class PlanetTerrainGenerator {

	private final PlanetGenerator parent;
	private final Map<AbsFace, SurfaceTerrainGenerator> surfaceGenerators;

	public PlanetTerrainGenerator(
		PlanetGenerator generator,
		SurfaceFloatField heightMap,
		FloatRangeMap<TerrainLayer> layers
	) {
		this.parent = generator;

		int seaLevel = (int) parent.getPlanet().getRadius();

		this.surfaceGenerators = AbsFace.mapToFaces(
			face -> new SurfaceTerrainGenerator(
				new Surface(face, seaLevel),
				heightMap,
				layers
			)
		);
	}

	public PlanetGenerator getGenerator() {
		return parent;
	}

	public DefaultChunkData generateTerrain(Server server, Vec3i chunkPos) {
		DefaultChunkData chunk = new DefaultChunkData(chunkPos, getGenerator().getWorldData());

		if (isOrdinaryChunk(chunkPos)) {
			generateOrdinaryTerrain(server, chunk);
		} else {
			generateBorderTerrain(server, chunk);
		}

		chunk.setGenerationHint(false);

		return chunk;
	}

	private boolean isOrdinaryChunk(Vec3i chunkPos) {
		Vec3i sorted = VectorUtil.sortAfterAbs(chunkPos, null);
		return sorted.x != sorted.y;
	}

	private void generateOrdinaryTerrain(Server server, DefaultChunkData chunk) {
		surfaceGenerators.get(chunk.getUp()).generateTerrain(server, chunk);
	}

	private void generateBorderTerrain(Server server, DefaultChunkData chunk) {
		BlockData stone = BlockDataRegistry.getInstance().get("Test:Stone");
		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");

		float radius = parent.getPlanet().getRadius();

		Vec3 biw = new Vec3();

		GenericChunks.forEachBiC(bic -> {

			biw.set(
				Coordinates.getInWorld(chunk.getX(), bic.x),
				Coordinates.getInWorld(chunk.getY(), bic.y),
				Coordinates.getInWorld(chunk.getZ(), bic.z)
			);

			biw.sub(DefaultChunkData.CHUNK_RADIUS - 0.5f);
			VectorUtil.sortAfterAbs(biw, biw);

			chunk.setBlock(bic, biw.x <= radius ? stone : air, false);

		});
	}

}
