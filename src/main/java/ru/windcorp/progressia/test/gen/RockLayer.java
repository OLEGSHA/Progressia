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

import ru.windcorp.progressia.common.util.noise.discrete.DiscreteNoise;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFloatField;
import ru.windcorp.progressia.server.world.generation.surface.TerrainLayer;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceBlockContext;

public class RockLayer implements TerrainLayer {

	private final DiscreteNoise<TerrainLayer> strata;
	private final SurfaceFloatField depthOffsets;
	
	private final double horizontalScale = 200;
	private final double verticalScale = 10;
	private final double depthInfluense = 0.1;

	public RockLayer(DiscreteNoise<TerrainLayer> strata, SurfaceFloatField depthOffsets) {
		this.strata = strata;
		this.depthOffsets = depthOffsets;
	}

	@Override
	public BlockData get(SurfaceBlockContext context, float depth) {
		
		double z = context.getLocation().z;
		z -= depth * depthInfluense;
		z += depthOffsets.get(context);
		z /= verticalScale;
		
		return strata
			.get(
				context.getLocation().x / horizontalScale,
				context.getLocation().y / horizontalScale,
				z
			)
			.get(context, depth);
	}

}
