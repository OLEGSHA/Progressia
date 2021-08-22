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

import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.server.world.generation.planet.Planet;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFloatField;

public class TestHeightMap implements SurfaceFloatField {
	
	private final SurfaceFloatField shape;

	public TestHeightMap(
		Planet planet,
		float cutoffThickness,
		Fields fields
	) {
		
		for (AbsFace face : AbsFace.getFaces()) {
			
			Field landmassDistribution = scale(octaves(fields.primitive(), 2, 5), 400);
			Field landmasses = tweak(squash(landmassDistribution, 4), 1, 20, -20);
			
			Field plainsSelector = squash(withMin(landmassDistribution, 0), 10);
			Field plains = tweak(octaves(fields.primitive(), 2, 3), 100, 3);
			
			Field randomCliffSelector = scale(fields.primitive(), 200);
			randomCliffSelector = add(
				select(randomCliffSelector, +0.7, 0.3),
				amplify(select(randomCliffSelector, -0.7, 0.3), -1)
			);
			Field randomCliffs = octaves(scale(fields.primitive(), 300), 3, 5);
			
			Field shoreCliffSelector = withMin(scale(fields.primitive(), 200), 0);
			Field shoreCliffs = add(
				landmassDistribution,
				tweak(octaves(fields.primitive(), 2, 3), 50, 0.2)
			);
			
			fields.register("Test:CliffSelector", face, multiply(
				shoreCliffSelector,
				bias(select(shoreCliffs, 0, 0.07), 0)
			));
			
			fields.register("Test:Height", face, cutoff(add(
				landmasses,
				multiply(plains, plainsSelector),
				multiply(amplify(cliff(randomCliffs, 0, 0.5, 0.03), 10), randomCliffSelector),
				multiply(tweak(cliff(shoreCliffs, 0, 0.5, 0.03), 1, 15, 15), shoreCliffSelector)
			), planet, cutoffThickness));
			
		}
		
		this.shape = fields.get("Test:Height");
	}

	@Override
	public float get(AbsFace face, float x, float y) {
		return (float) shape.get(face, x, y);
	}

}
