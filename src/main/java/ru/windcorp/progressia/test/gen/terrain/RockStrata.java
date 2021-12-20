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
package ru.windcorp.progressia.test.gen.terrain;

import ru.windcorp.progressia.common.util.noise.discrete.DiscreteNoise;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFloatField;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceBlockContext;
import ru.windcorp.progressia.test.Rocks.Rock;

public class RockStrata {
	
	private final DiscreteNoise<Rock> distribution;
	private final SurfaceFloatField depthOffsets;
	
	private final double horizontalScale = 800;
	private final double verticalScale = 20;
	private final double depthInfluence = 0.1;

	public RockStrata(DiscreteNoise<Rock> distribution, SurfaceFloatField depthOffsets) {
		this.distribution = distribution;
		this.depthOffsets = depthOffsets;
	}
	
	public Rock get(SurfaceBlockContext context, float depth) {
		double z = context.getLocation().z;
		z -= depth * depthInfluence;
		z += depthOffsets.get(context);
		z /= verticalScale;
		
		return distribution.get(
				context.getLocation().x / horizontalScale,
				context.getLocation().y / horizontalScale,
				z
		);
	}

}
