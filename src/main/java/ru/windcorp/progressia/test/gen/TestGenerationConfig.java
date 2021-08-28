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
import ru.windcorp.progressia.common.util.ArrayFloatRangeMap;
import ru.windcorp.progressia.common.util.FloatRangeMap;
import ru.windcorp.progressia.common.util.noise.discrete.WorleyProceduralNoise;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.generation.WorldGenerator;
import ru.windcorp.progressia.server.world.generation.planet.Planet;
import ru.windcorp.progressia.server.world.generation.planet.PlanetGenerator;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFeature;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFloatField;
import ru.windcorp.progressia.server.world.generation.surface.TerrainLayer;
import ru.windcorp.progressia.test.Rocks.RockVariant;
import ru.windcorp.progressia.test.TestContent;

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

		FloatRangeMap<TerrainLayer> layers = new ArrayFloatRangeMap<>();
		registerTerrainLayers(layers);

		List<SurfaceFeature> features = new ArrayList<>();
		registerFeatures(features);

		return server -> new PlanetGenerator("Test:PlanetGenerator", server, planet, heightMap, layers, features);

	}

	private void registerTerrainLayers(FloatRangeMap<TerrainLayer> layers) {
		BlockData dirt = BlockDataRegistry.getInstance().get("Test:Dirt");
		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");

		SurfaceFloatField cliffs = fields.get("Test:Cliff");

		WorleyProceduralNoise.Builder<TerrainLayer> builder = WorleyProceduralNoise.builder();
		TestContent.ROCKS.getRocks().forEach(rock -> {
			builder.add((c, d) -> {
				if (c.getRandom().nextInt(3) == 0) {
					return rock.getBlock(RockVariant.CRACKED);
				} else {
					return rock.getBlock(RockVariant.MONOLITH);
				}
			}, 1);
		});
		SurfaceFloatField rockDepthOffsets = fields.register(
			"Test:RockDepthOffsets",
			() -> tweak(fields.primitive(), 40, 5)
		);
		RockLayer rockLayer = new RockLayer(builder.build(SEED), rockDepthOffsets);

		layers.put(Float.NEGATIVE_INFINITY, 0, (c, d) -> air);
		layers.put(0, 4, (c, d) -> {
			if (cliffs.get(c.getSurface().getUp(), c.getLocation().x, c.getLocation().y) > 0) {
				return rockLayer.get(c, d);
			} else {
				return dirt;
			}
		});
		layers.put(4, Float.POSITIVE_INFINITY, rockLayer);
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

		Function<String, SurfaceFloatField> floweriness = flowerName -> fields.register(
			"Test:Flower" + flowerName,
			f -> multiply(
				selectPositive(squash(scale(octaves(fields.primitive(), 2, 3), 100), 2), 1, 0.5),
				tweak(fields.get("Test:Forest", f), 1, -1, 1.1),
				anti(squash(fields.get("Test:Cliff", f), 10))
			)
		);

		features.add(new TestBushFeature("Test:BushFeature", forestiness));
		features.add(new TestTreeFeature("Test:TreeFeature", forestiness));
		features.add(new TestGrassFeature("Test:GrassFeature", grassiness));
		features.add(new TestFlowerFeature("Test:FlowerFeature", floweriness));
	}

}
