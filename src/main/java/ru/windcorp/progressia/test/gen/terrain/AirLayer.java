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
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceBlockContext;

public class AirLayer extends TerrainLayer {
	
	private final BlockData air = BlockDataRegistry.getInstance().get("Test:Air");

	public AirLayer(String id) {
		super(id);
	}

	@Override
	public BlockData generate(SurfaceBlockContext context, float depth, float intensity) {
		return air;
	}

	@Override
	public float getIntensity(SurfaceBlockContext context, float depth) {
		return depth <= 0 ? 1 : 0;
	}

}
