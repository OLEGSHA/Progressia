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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.FloatRangeMap;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.generation.AbstractWorldGenerator;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFeature;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFloatField;
import ru.windcorp.progressia.server.world.generation.surface.TerrainLayer;

public class PlanetGenerator extends AbstractWorldGenerator<Boolean> {

	private final Planet planet;

	private final PlanetTerrainGenerator terrainGenerator;
	private final PlanetFeatureGenerator featureGenerator;

	public PlanetGenerator(
		String id,
		Server server,
		Planet planet,
		SurfaceFloatField heightMap,
		FloatRangeMap<TerrainLayer> layers,
		List<SurfaceFeature> features
	) {
		super(id, server, Boolean.class, "Test:PlanetGravityModel");

		this.planet = planet;

		PlanetGravityModel model = (PlanetGravityModel) this.getGravityModel();
		model.configure(planet.getGravityModelSettings());

		this.terrainGenerator = new PlanetTerrainGenerator(this, heightMap, layers);
		this.featureGenerator = new PlanetFeatureGenerator(this, features);
		
		
	}

	/**
	 * @return the planet
	 */
	public Planet getPlanet() {
		return planet;
	}

	@Override
	public Vec3 suggestSpawnLocation() {
		return new Vec3(7f, 1f, getPlanet().getRadius() + 10);
	}

	@Override
	protected Boolean doReadGenerationHint(DataInputStream input) throws IOException, DecodingException {
		return input.readBoolean();
	}

	@Override
	protected void doWriteGenerationHint(DataOutputStream output, Boolean hint) throws IOException {
		output.writeBoolean(hint);
	}

	@Override
	protected boolean checkIsChunkReady(Boolean hint) {
		return Boolean.TRUE.equals(hint); // Avoid NPE
	}

	@Override
	public DefaultChunkData generate(Vec3i chunkPos) {
		VectorUtil.iterateCuboidAround(chunkPos, 3, r -> conjureTerrain(r));
		DefaultChunkData chunk = getWorldData().getChunk(chunkPos);

		if (!isChunkReady(chunk.getGenerationHint())) {
			featureGenerator.generateFeatures(getServer(), chunk);
		}

		return chunk;
	}

	private void conjureTerrain(Vec3i chunkPos) {
		getServer().getLoadManager().getChunkManager().loadChunk(chunkPos);
		DefaultChunkData chunk = getWorldData().getChunk(chunkPos);

		if (chunk == null) {
			chunk = terrainGenerator.generateTerrain(getServer(), chunkPos);
			getWorldData().addChunk(chunk);
		}
	}

}
