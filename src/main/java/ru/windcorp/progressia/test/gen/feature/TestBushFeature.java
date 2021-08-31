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

public class TestBushFeature extends MultiblockVegetationFeature {

	private final BlockData trunk = BlockDataRegistry.getInstance().get("Test:Log");
	private final BlockData leaves = BlockDataRegistry.getInstance().get("Test:TemporaryLeaves");

	public TestBushFeature(String id, SurfaceFloatField selector) {
		super(id, selector, 7 * 7);
	}

	@Override
	protected void grow(SurfaceBlockContext context, double selectorValue) {
		double size = selectorValue * randomDouble(context, 0.8, 1.2);

		Vec3i center = context.getLocation().add_(0, 0, 1);

		context.setBlock(center, trunk);
		context.setBlock(center.add_(0, 0, 1), leaves);

		iterateBlob(center, stretch(size, 1.3, 2.5), stretch(size, 0.6, 1.5), 0.7, 2, p -> {
			setLeaves(context, p, leaves);
		});
	}

}
