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

public class TestGenerationConfig {

	private static final float PLANET_RADIUS = Units.get("0.5 km");
	private static final float SURFACE_GRAVITY = Units.get("9.8 m/s^2");
	private static final float CURVATURE = Units.get("100 m");
	private static final float INNER_RADIUS = Units.get("200 m");

	private static final Fields FIELDS = new Fields("No bugs please".hashCode());

	public static Function<Server, WorldGenerator> createGenerator() {

		Planet planet = new Planet(
			((int) PLANET_RADIUS) / Coordinates.CHUNK_SIZE,
			SURFACE_GRAVITY,
			CURVATURE,
			INNER_RADIUS
		);

		TestHeightMap heightMap = new TestHeightMap(planet, planet.getRadius() / 4, FIELDS);

		FloatRangeMap<TerrainLayer> layers = new ArrayFloatRangeMap<>();
		registerTerrainLayers(layers);

		List<SurfaceFeature> features = new ArrayList<>();
		registerFeatures(features);

		return server -> new PlanetGenerator("Test:PlanetGenerator", server, planet, heightMap, layers, features);

	}

	private static void registerTerrainLayers(FloatRangeMap<TerrainLayer> layers) {
		BlockData granite = BlockDataRegistry.getInstance().get("Test:GraniteMonolith");
		BlockData graniteCracked = BlockDataRegistry.getInstance().get("Test:GraniteCracked");
		BlockData graniteGravel = BlockDataRegistry.getInstance().get("Test:GraniteGravel");

		BlockData dirt = BlockDataRegistry.getInstance().get("Test:Dirt");
		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");

		SurfaceFloatField cliffs = FIELDS.get("Test:CliffSelector");

		layers.put(Float.NEGATIVE_INFINITY, 0, (f, n, w, d, r) -> air);
		layers.put(0, 4, (f, n, w, d, r) -> {
			if (cliffs.get(f, n, w) > 0) {
				switch (r.nextInt(4)) {
				case 0:
					return granite;
				case 1:
					return graniteCracked;
				default:
					return graniteGravel;
				}
			} else {
				return dirt;
			}
		});
		layers.put(4, Float.POSITIVE_INFINITY, (f, n, w, d, r) -> granite);
	}

	private static void registerFeatures(List<SurfaceFeature> features) {

		SurfaceFloatField forestiness = FIELDS.register(
			"Test:Forestiness",
			() -> squash(scale(FIELDS.primitive(), 200), 5)
		);
		
		SurfaceFloatField floweriness = FIELDS.register(
			"Test:Floweriness",
			f -> multiply(
				scale(octaves(FIELDS.primitive(), 2, 2), 40),
				tweak(FIELDS.get("Test:Forestiness", f), 1, -1, 1.1)
			)
		);

		features.add(new TestBushFeature("Test:BushFeature", forestiness));
		features.add(new TestTreeFeature("Test:TreeFeature", forestiness));
		features.add(new TestGrassFeature("Test:GrassFeature", FIELDS.get("Test:CliffSelector"), floweriness));
	}

}
