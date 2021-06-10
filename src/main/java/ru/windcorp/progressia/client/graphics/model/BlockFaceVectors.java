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

package ru.windcorp.progressia.client.graphics.model;

import static ru.windcorp.progressia.common.world.block.BlockFace.*;

import com.google.common.collect.ImmutableMap;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.common.world.block.BlockFace;

class BlockFaceVectors {

	private static BlockFaceVectors createInner(BlockFaceVectors outer) {
		ImmutableMap.Builder<BlockFace, Vec3> originBuilder = ImmutableMap.builder();

		ImmutableMap.Builder<BlockFace, Vec3> widthBuilder = ImmutableMap.builder();

		ImmutableMap.Builder<BlockFace, Vec3> heightBuilder = ImmutableMap.builder();

		for (BlockFace face : getFaces()) {
			Vec3 width = outer.getWidth(face);
			Vec3 height = outer.getHeight(face);

			originBuilder.put(face, new Vec3(outer.getOrigin(face)));

			widthBuilder.put(face, new Vec3(width));
			heightBuilder.put(face, new Vec3(height));
		}

		return new BlockFaceVectors(originBuilder.build(), widthBuilder.build(), heightBuilder.build());
	}

	private static final BlockFaceVectors OUTER;
	private static final BlockFaceVectors INNER;

	static {
		OUTER = new BlockFaceVectors(ImmutableMap.<BlockFace, Vec3>builder()

				.put(TOP, new Vec3(-0.5f, +0.5f, +0.5f)).put(BOTTOM, new Vec3(-0.5f, -0.5f, -0.5f))
				.put(NORTH, new Vec3(+0.5f, -0.5f, -0.5f)).put(SOUTH, new Vec3(-0.5f, +0.5f, -0.5f))
				.put(WEST, new Vec3(+0.5f, +0.5f, -0.5f)).put(EAST, new Vec3(-0.5f, -0.5f, -0.5f))

				.build(),

				ImmutableMap.<BlockFace, Vec3>builder()

						.put(TOP, new Vec3(0, -1, 0)).put(BOTTOM, new Vec3(0, +1, 0)).put(NORTH, new Vec3(0, +1, 0))
						.put(SOUTH, new Vec3(0, -1, 0)).put(WEST, new Vec3(-1, 0, 0)).put(EAST, new Vec3(+1, 0, 0))

						.build(),

				ImmutableMap.<BlockFace, Vec3>builder()

						.put(TOP, new Vec3(+1, 0, 0)).put(BOTTOM, new Vec3(+1, 0, 0)).put(NORTH, new Vec3(0, 0, +1))
						.put(SOUTH, new Vec3(0, 0, +1)).put(WEST, new Vec3(0, 0, +1)).put(EAST, new Vec3(0, 0, +1))

						.build());

		INNER = createInner(OUTER);
	}

	public static BlockFaceVectors get(boolean inner) {
		return inner ? INNER : OUTER;
	}

	private final ImmutableMap<BlockFace, Vec3> origins;
	private final ImmutableMap<BlockFace, Vec3> widths;
	private final ImmutableMap<BlockFace, Vec3> heights;

	public BlockFaceVectors(ImmutableMap<BlockFace, Vec3> origins, ImmutableMap<BlockFace, Vec3> widths,
			ImmutableMap<BlockFace, Vec3> heights) {
		this.origins = origins;
		this.widths = widths;
		this.heights = heights;
	}

	public Vec3 getOrigin(BlockFace face) {
		return origins.get(face);
	}

	public Vec3 getWidth(BlockFace face) {
		return widths.get(face);
	}

	public Vec3 getHeight(BlockFace face) {
		return heights.get(face);
	}
}
