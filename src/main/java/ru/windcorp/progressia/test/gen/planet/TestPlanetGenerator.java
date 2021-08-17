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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.generation.AbstractWorldGenerator;

public class TestPlanetGenerator extends AbstractWorldGenerator<Boolean> {
	
	private final Planet planet;
	
	private final PlanetTerrainGenerator terrainGenerator;
	private final PlanetFeatureGenerator featureGenerator;

	public TestPlanetGenerator(String id, Server server, Planet planet) {
		super(id, server, Boolean.class, "Test:PlanetGravityModel");
		
		this.planet = planet;
		
		TestPlanetGravityModel model = (TestPlanetGravityModel) this.getGravityModel();
		model.configure(planet.getGravityModelSettings());
		
		this.terrainGenerator = new PlanetTerrainGenerator(this);
		this.featureGenerator = new PlanetFeatureGenerator(this);
	}
	
	/**
	 * @return the planet
	 */
	public Planet getPlanet() {
		return planet;
	}
	
	@Override
	public Vec3 suggestSpawnLocation() {
		return new Vec3(7f, 7f, getPlanet().getRadius() + 10);
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
		DefaultChunkData chunk = getWorldData().getChunk(chunkPos);
		
		if (chunk == null) {
			chunk = getWorldData().getChunk(chunkPos);
		}

		if (chunk == null) {
			chunk = terrainGenerator.generateTerrain(chunkPos);
			getWorldData().addChunk(chunk);
		}
	}

}
