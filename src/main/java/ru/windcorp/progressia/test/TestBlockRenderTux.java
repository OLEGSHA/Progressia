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

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeParts;
import ru.windcorp.progressia.client.graphics.model.Shapes;
import ru.windcorp.progressia.client.graphics.model.StaticModel;
import ru.windcorp.progressia.client.graphics.texture.ComplexTexture;
import ru.windcorp.progressia.client.graphics.texture.SimpleTextures;
import ru.windcorp.progressia.client.graphics.texture.TexturePrimitive;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.block.BlockRender;
import ru.windcorp.progressia.common.world.DefaultChunkData;

public class TestBlockRenderTux extends BlockRender {

	private final Renderable model;

	public TestBlockRenderTux(String id) {
		super(id);

		TexturePrimitive primitive = SimpleTextures.get("blocks/Tux").getSprite().getPrimitive();
		ComplexTexture texture = new ComplexTexture(primitive, 72, 60);

		WorldRenderProgram program = WorldRenderProgram.getDefault();
		StaticModel.Builder builder = StaticModel.builder();

		final float scale = 1f / 40;
		final float lift = -1f / 2 / scale;

		builder.addPart(
			new Shapes.PppBuilder(program, texture.getCuboidTextures(0, 36, 12)).setSize(12)
				.centerAt(0, 0, 18 + (12 / 2) + lift).scale(scale).create()
		);
		builder.addPart(
			new Shapes.PppBuilder(program, texture.getCuboidTextures(0, 0, 18)).setSize(18)
				.centerAt(0, 0, 18 / 2 + lift).scale(scale).create()
		);
		builder.addPart(
			new Shape(
				Usage.STATIC,
				program,
				ShapeParts.createRectangle(
					program,
					texture.get(48, 44, 24, 16),
					Colors.WHITE,
					new Vec3(18 / 2 + 1, -(24 / 2), lift),
					new Vec3(0, 24, 0),
					new Vec3(0, 0, 16),
					false
				),
				ShapeParts.createRectangle(
					program,
					texture.get(48, 44, 24, 16),
					Colors.WHITE,
					new Vec3(18 / 2 + 1, -(24 / 2), lift),
					new Vec3(0, 24, 0),
					new Vec3(0, 0, 16),
					true
				)
			),
			new Mat4().scale(scale)
		);

		this.model = builder.build();
	}

	@Override
	public Renderable createRenderable(DefaultChunkData chunk, Vec3i relBlockInChunk) {
		return model;
	}

	@Override
	public boolean needsOwnRenderable() {
		return true;
	}

}
