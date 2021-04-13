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
package ru.windcorp.progressia.test;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.test.gen.surface.SurfaceFeature;
import ru.windcorp.progressia.test.gen.surface.SurfaceWorld;

public class TestBushFeature extends SurfaceFeature {

	public TestBushFeature(String id) {
		super(id);
	}

	@Override
	public void process(SurfaceWorld world, Request request) {
		BlockData block = BlockDataRegistry.getInstance().get("Test:Log");
		
		Vec3i location = new Vec3i(request.getRandom().nextInt(ChunkData.BLOCKS_PER_CHUNK), request.getRandom().nextInt(ChunkData.BLOCKS_PER_CHUNK), request.getRandom().nextInt(ChunkData.BLOCKS_PER_CHUNK)).add(request.getMin());
		if (world.getBlockSfc(location) == BlockDataRegistry.getInstance().get("Test:Air")) {
			
			world.setBlockSfc(location, block, false);
			
		}
	}

}
