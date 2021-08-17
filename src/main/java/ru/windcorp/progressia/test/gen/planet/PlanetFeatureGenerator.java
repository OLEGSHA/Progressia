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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.test.TestBushFeature;
import ru.windcorp.progressia.test.TestGrassFeature;
import ru.windcorp.progressia.test.TestTreeFeature;
import ru.windcorp.progressia.test.gen.surface.Surface;
import ru.windcorp.progressia.test.gen.surface.SurfaceFeature;
import ru.windcorp.progressia.test.gen.surface.SurfaceFeatureGenerator;

public class PlanetFeatureGenerator {

	private final TestPlanetGenerator parent;
	
	private final Map<AbsFace, SurfaceFeatureGenerator> surfaceGenerators;

	public PlanetFeatureGenerator(TestPlanetGenerator generator) {
		this.parent = generator;

		Collection<SurfaceFeature> features = new ArrayList<>();
		features.add(new TestBushFeature("Test:BushFeature"));
		features.add(new TestTreeFeature("Test:TreeFeature"));
		features.add(new TestGrassFeature("Test:GrassFeature"));
		
		int seaLevel = (int) parent.getPlanet().getRadius();
		this.surfaceGenerators = AbsFace.mapToFaces(face -> new SurfaceFeatureGenerator(
			new Surface(face, seaLevel),
			features
		));
	}

	public TestPlanetGenerator getGenerator() {
		return parent;
	}

	public void generateFeatures(Server server, DefaultChunkData chunk) {
		if (isOrdinaryChunk(chunk.getPosition())) {
			generateOrdinaryFeatures(server, chunk);
		} else {
			generateBorderFeatures(server, chunk);
		}
		
		chunk.setGenerationHint(true);
	}

	private boolean isOrdinaryChunk(Vec3i chunkPos) {
		Vec3i sorted = VectorUtil.sortAfterAbs(chunkPos, null);
		return sorted.x != sorted.y;
	}

	private void generateOrdinaryFeatures(Server server, DefaultChunkData chunk) {
		surfaceGenerators.get(chunk.getUp()).generateFeatures(server, chunk);
	}

	private void generateBorderFeatures(Server server, DefaultChunkData chunk) {
		// Do nothing
	}

}
