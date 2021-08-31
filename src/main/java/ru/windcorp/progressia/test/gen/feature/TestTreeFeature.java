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
package ru.windcorp.progressia.test.gen.feature;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.server.world.generation.surface.SurfaceFloatField;
import ru.windcorp.progressia.server.world.generation.surface.context.SurfaceBlockContext;

public class TestTreeFeature extends MultiblockVegetationFeature {
	
	private final BlockData trunk = BlockDataRegistry.getInstance().get("Test:Log");
	private final BlockData leaves = BlockDataRegistry.getInstance().get("Test:TemporaryLeaves");

	public TestTreeFeature(String id, SurfaceFloatField selector) {
		super(id, selector, 10 * 10);
	}
	
	@Override
	protected void grow(SurfaceBlockContext context, double selectorValue) {
		
		Vec3i start = context.getLocation().add_(0, 0, 1);
		Vec3i center = start.add_(0);

		double size = selectorValue * randomDouble(context, 0.8, 1.2);
		
		int height = (int) stretch(size, 3, 7);
		for (; center.z < start.z + height; ++center.z) {
			context.setBlock(center, trunk);
		}
		
		double branchHorDistance = 0;

		do {
			double branchSize = 0.5 + randomDouble(context, 1, 2) * size;
			double branchHorAngle = randomDouble(context, 0, 2 * Math.PI);
			int branchVertOffset = (int) randomDouble(context, -2, 0);

			Vec3i branchCenter = center.add_(
				(int) (Math.sin(branchHorAngle) * branchHorDistance),
				(int) (Math.cos(branchHorAngle) * branchHorDistance),
				branchVertOffset
			);

			iterateBlob(branchCenter, 1 * branchSize, 2.3 * branchSize, 0.5, 3, p -> {
				setLeaves(context, p, leaves);
			});

			branchHorDistance = randomDouble(context, 0.7, 1.5);
		} while (context.getRandom().nextInt(8) > 1);
	}

}
