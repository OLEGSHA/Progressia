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
package ru.windcorp.progressia.test.gen;

import static ru.windcorp.progressia.test.gen.Fields.*;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.util.noise.discrete.WorleyProceduralNoise;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.generation.WorldGenerator;
import ru.windcorp.progressia.server.world.generation.planet.Planet;
import ru.windcorp.progressia.server.world.generation.planet.PlanetGenerator;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFeature;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFloatField;
import ru.windcorp.progressia.test.Rocks.Rock;
import ru.windcorp.progressia.test.TestContent;
import ru.windcorp.progressia.test.gen.feature.*;
import ru.windcorp.progressia.test.gen.terrain.*;

public class TestGenerationConfig {

	private static final long SEED = "No bugs please".hashCode();

	private static final float PLANET_RADIUS = Units.get("0.5 km");
	private static final float SURFACE_GRAVITY = Units.get("9.8 m/s^2");
	private static final float CURVATURE = Units.get("100 m");
	private static final float INNER_RADIUS = Units.get("200 m");

	private final Fields fields = new Fields(SEED);
	private final Function<Server, WorldGenerator> generator;
	
	public TestGenerationConfig() {
		this.generator = createGenerator();
	}
	
	public Function<Server, WorldGenerator> getGenerator() {
		return generator;
	}

	private Function<Server, WorldGenerator> createGenerator() {

		Planet planet = new Planet(
			((int) PLANET_RADIUS) / Coordinates.CHUNK_SIZE,
			SURFACE_GRAVITY,
			CURVATURE,
			INNER_RADIUS
		);

		TestHeightMap heightMap = new TestHeightMap(planet, planet.getRadius() / 4, fields);

		LayeredTerrain terrain = new LayeredTerrain();
		registerTerrainLayers(terrain);

		List<SurfaceFeature> features = new ArrayList<>();
		registerFeatures(features);

		return server -> new PlanetGenerator("Test:PlanetGenerator", server, planet, heightMap, terrain, features);

	}

	private void registerTerrainLayers(LayeredTerrain terrain) {
		SurfaceFloatField cliffs = fields.get("Test:Cliff");
		SurfaceFloatField beaches = fields.register(
			"Test:Beach",
			f -> multiply(
				anti(fields.get("Test:Cliff", f))
			)
		);
		RockStrata rockStrata = createStrata();

		terrain.addLayer(new AirLayer("Test:Air"));
		terrain.addLayer(new MantleLayer("Test:Mantle"));
		terrain.addLayer(new CrustLayer("Test:Crust", rockStrata));
		terrain.addLayer(new WaterLayer("Test:Water"));
		terrain.addLayer(new SoilLayer("Test:Soil"));
		terrain.addLayer(new CliffLayer("Test:Cliffs", cliffs, rockStrata));
		terrain.addLayer(new BeachLayer("Test:Beaches", beaches, rockStrata));
	}
	
	private RockStrata createStrata() {
		WorleyProceduralNoise.Builder<Rock> builder = WorleyProceduralNoise.builder();
		TestContent.ROCKS.getRocks().forEach(rock -> builder.add(rock, 1));
		
		SurfaceFloatField rockDepthOffsets = fields.register(
			"Test:RockDepthOffsets",
			() -> tweak(fields.primitive(), 40, 5)
		);
		
		return new RockStrata(builder.build(SEED), rockDepthOffsets);
	}

	private void registerFeatures(List<SurfaceFeature> features) {

		SurfaceFloatField forestiness = fields.register(
			"Test:Forest",
			() -> squash(scale(fields.primitive(), 200), 5)
		);
		
		SurfaceFloatField grassiness = fields.register(
			"Test:Grass",
			f -> multiply(
				tweak(octaves(fields.primitive(), 2, 2), 40, 0.5, 1.2),
				squash(tweak(fields.get("Test:Forest", f), 1, -1, 1), 10),
				anti(squash(fields.get("Test:Cliff", f), 10))
			)
		);

		Function<AbsFace, Field> floweriness = f -> multiply(
			amplify(withMin(squash(scale(octaves(fields.primitive(), 2, 3), 100), 5), 0), 2),
			tweak(fields.get("Test:Forest", f), 1, -1, 1.1),
			anti(squash(fields.get("Test:Cliff", f), 10))
		);

		features.add(new TestBushFeature("Test:BushFeature", forestiness));
		features.add(new TestTreeFeature("Test:TreeFeature", forestiness));
		features.add(new TestGrassFeature("Test:GrassFeature", grassiness));
		features.add(new TestFlowerFeature("Test:FlowerFeature", TestContent.FLOWERS, floweriness, fields));
	}

}
