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

import static ru.windcorp.progressia.common.world.block.AbsFace.*;

import com.google.common.collect.ImmutableMap;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.common.world.block.AbsFace;

class BlockFaceVectors {

	private static BlockFaceVectors createInner(BlockFaceVectors outer) {
		ImmutableMap.Builder<AbsFace, Vec3> originBuilder = ImmutableMap.builder();

		ImmutableMap.Builder<AbsFace, Vec3> widthBuilder = ImmutableMap.builder();

		ImmutableMap.Builder<AbsFace, Vec3> heightBuilder = ImmutableMap.builder();

		for (AbsFace face : getFaces()) {
			Vec3 width = outer.getWidth(face);
			Vec3 height = outer.getHeight(face);

			originBuilder.put(
				face,
				new Vec3(outer.getOrigin(face))
			);

			widthBuilder.put(face, new Vec3(width));
			heightBuilder.put(face, new Vec3(height));
		}

		return new BlockFaceVectors(
			originBuilder.build(),
			widthBuilder.build(),
			heightBuilder.build()
		);
	}

	private static final BlockFaceVectors OUTER;
	private static final BlockFaceVectors INNER;

	static {
		OUTER = new BlockFaceVectors(
			ImmutableMap.<AbsFace, Vec3>builder()

				.put(POS_Z, new Vec3(-0.5f, +0.5f, +0.5f))
				.put(NEG_Z, new Vec3(-0.5f, -0.5f, -0.5f))
				.put(POS_X, new Vec3(+0.5f, -0.5f, -0.5f))
				.put(NEG_X, new Vec3(-0.5f, +0.5f, -0.5f))
				.put(POS_Y, new Vec3(+0.5f, +0.5f, -0.5f))
				.put(NEG_Y, new Vec3(-0.5f, -0.5f, -0.5f))

				.build(),

			ImmutableMap.<AbsFace, Vec3>builder()

				.put(POS_Z, new Vec3(0, -1, 0))
				.put(NEG_Z, new Vec3(0, +1, 0))
				.put(POS_X, new Vec3(0, +1, 0))
				.put(NEG_X, new Vec3(0, -1, 0))
				.put(POS_Y, new Vec3(-1, 0, 0))
				.put(NEG_Y, new Vec3(+1, 0, 0))

				.build(),

			ImmutableMap.<AbsFace, Vec3>builder()

				.put(POS_Z, new Vec3(+1, 0, 0))
				.put(NEG_Z, new Vec3(+1, 0, 0))
				.put(POS_X, new Vec3(0, 0, +1))
				.put(NEG_X, new Vec3(0, 0, +1))
				.put(POS_Y, new Vec3(0, 0, +1))
				.put(NEG_Y, new Vec3(0, 0, +1))

				.build()
		);

		INNER = createInner(OUTER);
	}

	public static BlockFaceVectors get(boolean inner) {
		return inner ? INNER : OUTER;
	}

	private final ImmutableMap<AbsFace, Vec3> origins;
	private final ImmutableMap<AbsFace, Vec3> widths;
	private final ImmutableMap<AbsFace, Vec3> heights;

	public BlockFaceVectors(
		ImmutableMap<AbsFace, Vec3> origins,
		ImmutableMap<AbsFace, Vec3> widths,
		ImmutableMap<AbsFace, Vec3> heights
	) {
		this.origins = origins;
		this.widths = widths;
		this.heights = heights;
	}

	public Vec3 getOrigin(AbsFace face) {
		return origins.get(face);
	}

	public Vec3 getWidth(AbsFace face) {
		return widths.get(face);
	}

	public Vec3 getHeight(AbsFace face) {
		return heights.get(face);
	}
}
