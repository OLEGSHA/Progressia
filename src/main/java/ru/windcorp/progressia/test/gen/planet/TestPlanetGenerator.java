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
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.server.world.WorldLogic;
import ru.windcorp.progressia.server.world.generation.AbstractWorldGenerator;

public class TestPlanetGenerator extends AbstractWorldGenerator<Boolean> {
	
	private final Planet planet;
	
	private final PlanetTerrainGenerator terrainGenerator;
	private final PlanetScatterGenerator scatterGenerator;

	public TestPlanetGenerator(String id, Planet planet, WorldLogic world) {
		super(id, Boolean.class, "Test:PlanetGravityModel");
		this.planet = planet;
		
		this.terrainGenerator = new PlanetTerrainGenerator(this);
		this.scatterGenerator = new PlanetScatterGenerator(this);
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
		return hint;
	}

	@Override
	public ChunkData generate(Vec3i chunkPos, WorldData world) {
		ChunkData chunk = terrainGenerator.generateTerrain(chunkPos, world);
		scatterGenerator.generateScatter(chunk);
		return chunk;
	}

}
