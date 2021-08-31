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

import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.util.math.PiecewiseLinearFunction;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceBlockContext;

public class SoilLayer extends TerrainLayer {

	private static final PiecewiseLinearFunction THICKNESS = PiecewiseLinearFunction.builder()
		.add(Units.get("-5 m"), Units.get("1 m"))
		.add(Units.get("0 m"), Units.get("4 m"))
		.add(Units.get("5 km"), Units.get("0 m"))
		.build();
	
	private final BlockData soil = BlockDataRegistry.getInstance().get("Test:Dirt");

	public SoilLayer(String id) {
		super(id);
	}

	@Override
	public BlockData generate(SurfaceBlockContext context, float depth, float intensity) {
		return soil;
	}

	@Override
	public float getIntensity(SurfaceBlockContext context, float depth) {
		if (depth < 0) return 0;
		
		float altitude = context.getLocation().z;
		float thickness = THICKNESS.apply(altitude);
		
		if (depth < thickness) {
			return 2;
		} else {
			return 0;
		}
	}

}
