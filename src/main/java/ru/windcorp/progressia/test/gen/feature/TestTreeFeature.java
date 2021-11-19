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
	private final BlockData leaves = BlockDataRegistry.getInstance().get("Test:LeavesPine");

	public TestTreeFeature(String id, SurfaceFloatField selector) {
		super(id, selector, 7 * 7);
	}

	@Override
	protected void grow(SurfaceBlockContext context, double selectorValue) {

		Vec3i start = context.getLocation().add_(0, 0, 1);
		Vec3i center = start.add_(0);

		double size = selectorValue * randomDouble(context, 0.8, 1.2);
		double volume = stretch(size, 2.5, 1);

		int height = (int) stretch(size, 8, 12);
		for (; center.z < start.z + height; ++center.z) {
			context.setBlock(center, trunk);
		}
		--center.z;

		iterateBlob(center, 1.5 * volume, 1.75, 0.5, 3, p -> {
			setLeaves(context, p, leaves);
		});

		int branchCount = 1 + context.getRandom().nextInt(2) + (int) (stretch(size, 0, 4));

		for (int i = 0; i < branchCount; ++i) {

			double branchHorAngle = randomDouble(context, 0, 2 * Math.PI);
			double branchHorDistance = randomDouble(context, 0.7, 1.5) * volume;

			int branchVertOffset = (int) randomDouble(context, -height / 3.0, -height / 1.5);

			double branchSize = randomDouble(context, 1, 2) * volume;
			double branchHeight = 1.5;

			Vec3i branchCenter = center.add_(
				(int) (Math.sin(branchHorAngle) * branchHorDistance),
				(int) (Math.cos(branchHorAngle) * branchHorDistance),
				branchVertOffset
			);

			iterateBlob(branchCenter, branchSize, branchHeight, 0.5, 3, p -> {
				setLeaves(context, p, leaves);
			});

		}

	}

}
