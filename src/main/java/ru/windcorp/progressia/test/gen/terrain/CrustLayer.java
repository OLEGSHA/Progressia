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

import ru.windcorp.progressia.common.util.ArrayFloatRangeMap;
import ru.windcorp.progressia.common.util.FloatRangeMap;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceBlockContext;
import ru.windcorp.progressia.test.Rocks.RockVariant;

public class CrustLayer extends TerrainLayer {

	private static final FloatRangeMap<RockVariant> WEAR_TABLE = new ArrayFloatRangeMap<>();
	static {
		WEAR_TABLE.put(Float.NEGATIVE_INFINITY, 0.25f, RockVariant.MONOLITH);
		WEAR_TABLE.put(0.25f, 0.5f, RockVariant.CRACKED);
		WEAR_TABLE.put(0.5f, 0.75f, RockVariant.GRAVEL);
		WEAR_TABLE.put(0.75f, Float.POSITIVE_INFINITY, RockVariant.SAND);
	}
	
	private final RockStrata strata;

	public CrustLayer(String id, RockStrata strata) {
		super(id);
		this.strata = strata;
	}

	@Override
	public BlockData generate(SurfaceBlockContext context, float depth, float intensity) {
		
		RockVariant variant;
		if (depth < 8) {
			float wear = 1 - depth / 8;
			float offset = (context.getRandom().nextFloat() * 2 - 1) * 0.5f;
			variant = WEAR_TABLE.get(wear + offset);
		} else {
			variant = RockVariant.MONOLITH;
		}
		
		return strata.get(context, depth).getBlock(variant);
	}

	@Override
	public float getIntensity(SurfaceBlockContext context, float depth) {
		if (depth < 0) {
			return 0;
		} else if (context.getLocation().z > -100) {
			return 1;
		} else {
			return 0;
		}
	}

}
