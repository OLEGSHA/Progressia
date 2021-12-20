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

public class CliffLayer extends TerrainLayer {

	private final SurfaceFloatField cliffSelector;
	private final RockStrata strata;

	public CliffLayer(String id, SurfaceFloatField cliffSelector, RockStrata strata) {
		super(id);
		this.cliffSelector = cliffSelector;
		this.strata = strata;
	}

	@Override
	public BlockData generate(SurfaceBlockContext context, float depth, float intensity) {
		
		RockVariant variant;
		switch (context.getRandom().nextInt(4)) {
		case 0:
			variant = RockVariant.GRAVEL;
			break;
		case 1:
			variant = RockVariant.MONOLITH;
			break;
		default:
			variant = RockVariant.CRACKED;
			break;
		}
		
		return strata.get(context, depth).getBlock(variant);
	}

	@Override
	public float getIntensity(SurfaceBlockContext context, float depth) {
		if (depth < 0 || depth > 7) {
			return 0;
		}
		
		return 100 * cliffSelector.get(context);
	}

}
