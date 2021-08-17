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
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.test.gen.surface.SurfaceTopLayerFeature;
import ru.windcorp.progressia.test.gen.surface.context.SurfaceBlockContext;
import ru.windcorp.progressia.test.gen.surface.context.SurfaceWorldContext;

public class TestBushFeature extends SurfaceTopLayerFeature {

	public TestBushFeature(String id) {
		super(id);
	}
	
	private void tryToSetLeaves(SurfaceWorldContext context, Vec3i location, BlockData leaves) {
		if (context.getBlock(location).getId().equals("Test:Air")) {
			context.setBlock(location, leaves);
		}
	}
	
	@Override
	protected void processTopBlock(SurfaceBlockContext context) {
		if (context.getRandom().nextInt(10*10) > 0) return;
		
		Vec3i center = context.getLocation().add_(0, 0, 1);

		BlockData log = BlockDataRegistry.getInstance().get("Test:Log");
		BlockData leaves = BlockDataRegistry.getInstance().get("Test:TemporaryLeaves");
		
		context.setBlock(center, log);
		
		VectorUtil.iterateCuboidAround(center.x, center.y, center.z, 3, 3, 3, p -> {
			tryToSetLeaves(context, p, leaves);
		});
		
		VectorUtil.iterateCuboidAround(center.x, center.y, center.z, 5, 5, 1, p -> {
			tryToSetLeaves(context, p, leaves);
		});
	}
	
	@Override
	protected boolean isSolid(SurfaceBlockContext context) {
		return context.logic().getBlock().isSolid(RelFace.UP);
	}

}
