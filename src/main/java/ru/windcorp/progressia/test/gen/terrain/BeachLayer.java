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

import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFloatField;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceBlockContext;
import ru.windcorp.progressia.test.Rocks.RockVariant;

public class BeachLayer extends TerrainLayer {

	private final SurfaceFloatField beachSelector;
	private final RockStrata strata;

	public BeachLayer(String id, SurfaceFloatField beachSelector, RockStrata strata) {
		super(id);
		this.beachSelector = beachSelector;
		this.strata = strata;
	}

	@Override
	public BlockData generate(SurfaceBlockContext context, float depth, float intensity) {
		return strata.get(context, depth).getBlock(RockVariant.SAND);
	}

	@Override
	public float getIntensity(SurfaceBlockContext context, float depth) {
		if (depth < 0 || depth > 3) {
			return 0;
		}
		
		float altitude = context.getLocation().z;
		if (altitude < -5| altitude > 1) {
			return 0;
		}
		
		return 3 * beachSelector.get(context);
	}

}
