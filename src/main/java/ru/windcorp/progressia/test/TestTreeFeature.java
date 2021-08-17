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

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.test.gen.surface.SurfaceTopLayerFeature;
import ru.windcorp.progressia.test.gen.surface.context.SurfaceBlockContext;
import ru.windcorp.progressia.test.gen.surface.context.SurfaceWorldContext;

public class TestTreeFeature extends SurfaceTopLayerFeature {

	public TestTreeFeature(String id) {
		super(id);
	}

	private void tryToSetLeaves(SurfaceWorldContext context, Vec3i location, BlockData leaves) {
		if (context.getBlock(location).getId().equals("Test:Air")) {
			context.setBlock(location, leaves);
		}
	}

	private void iterateSpheroid(Vec3i center, double horDiameter, double vertDiameter, Consumer<Vec3i> action) {
		VectorUtil.iterateCuboidAround(
			center.x,
			center.y,
			center.z,
			(int) Math.ceil(horDiameter) / 2 * 2 + 5,
			(int) Math.ceil(horDiameter) / 2 * 2 + 5,
			(int) Math.ceil(vertDiameter) / 2 * 2 + 5,
			pos -> {
				double sx = (pos.x - center.x) / horDiameter;
				double sy = (pos.y - center.y) / horDiameter;
				double sz = (pos.z - center.z) / vertDiameter;

				if (sx * sx + sy * sy + sz * sz <= 1) {
					action.accept(pos);
				}
			}
		);
	}

	@Override
	protected void processTopBlock(SurfaceBlockContext context) {
		if (context.getRandom().nextInt(20 * 20) > 0)
			return;

		Vec3i start = context.getLocation().add_(0, 0, 1);

		BlockData log = BlockDataRegistry.getInstance().get("Test:Log");
		BlockData leaves = BlockDataRegistry.getInstance().get("Test:TemporaryLeaves");

		Vec3i center = start.add_(0);

		int height = context.getRandom().nextInt(3) + 5;
		for (; center.z < start.z + height; ++center.z) {
			context.setBlock(center, log);
		}

		double branchHorDistance = 0;

		do {
			double branchSize = 0.5 + 1 * context.getRandom().nextDouble();
			double branchHorAngle = 2 * Math.PI * context.getRandom().nextDouble();
			int branchVertOffset = -2 + context.getRandom().nextInt(3);

			Vec3i branchCenter = center.add_(
				(int) (Math.sin(branchHorAngle) * branchHorDistance),
				(int) (Math.cos(branchHorAngle) * branchHorDistance),
				branchVertOffset
			);

			iterateSpheroid(branchCenter, 1.75 * branchSize, 2.5 * branchSize, p -> {
				tryToSetLeaves(context, p, leaves);
			});

			branchHorDistance = 1 + 2 * context.getRandom().nextDouble();
		} while (context.getRandom().nextInt(8) > 1);
	}

	@Override
	protected boolean isSolid(SurfaceBlockContext context) {
		return context.logic().getBlock().isSolid(RelFace.UP);
	}

}
