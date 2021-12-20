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
package ru.windcorp.progressia.test.trees;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapePart;
import ru.windcorp.progressia.client.graphics.model.ShapePrototype;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.block.BlockRender;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerSimple.BlockOptimizedSimple;
import ru.windcorp.progressia.common.world.DefaultChunkData;

public class BlockRenderLeavesPine extends BlockRender implements BlockOptimizedSimple {

	private final Texture texture;

	public BlockRenderLeavesPine(String id, Texture texture) {
		super(id);
		this.texture = texture;
	}

	@Override
	public void getShapeParts(DefaultChunkData chunk, Vec3i bic, Consumer<ShapePart> output) {
		Random random = new Random(chunk.getX() ^ bic.x);
		random.setSeed(random.nextLong() ^ chunk.getY() ^ bic.y);
		random.setSeed(random.nextLong() ^ chunk.getZ() ^ bic.z);

		ShapePrototype sp = ShapePrototype.unitSquare(texture).makeDoubleSided();
		sp.push().translate(
			bic.x + random.nextFloat() * 0.3f - 0.15f,
			bic.y + random.nextFloat() * 0.3f - 0.15f,
			bic.z
		);

		final float angle = 0.5f;

		for (float offset : new float[] { -0.25f, -0.75f }) {
			sp.push().translate(0, 0, offset + random.nextFloat() * 0.5f)
				.rotateZ(-0.3f + random.nextFloat() * 0.6)
				.rotateX(-0.3f + random.nextFloat() * 0.6);

			sp.push().translate(0, 0, -0.3f).rotateX(angle).translate(-0.5f, -0.5f, 0.5f);
			output.accept(sp.build());
			sp.pop();

			sp.push().translate(0, 0, -0.3f).rotateX(-angle).translate(-0.5f, -0.5f, 0.5f);
			output.accept(sp.build());
			sp.pop();

			sp.push().translate(0, 0, -0.3f).rotateY(angle).translate(-0.5f, -0.5f, 0.5f);
			output.accept(sp.build());
			sp.pop();

			sp.push().translate(0, 0, -0.3f).rotateY(-angle).translate(-0.5f, -0.5f, 0.5f);
			output.accept(sp.build());
			sp.pop();

			sp.pop();
			sp.addColorMultiplier(Colors.GRAY_A);
		}
	}

	@Override
	public Renderable createRenderable(DefaultChunkData chunk, Vec3i blockInChunk) {
		Collection<ShapePart> parts = new ArrayList<>(6);

		getShapeParts(chunk, blockInChunk, parts::add);

		return new Shape(
			Usage.STATIC,
			WorldRenderProgram.getDefault(),
			parts.toArray(new ShapePart[parts.size()])
		);
	}

	@Override
	public boolean needsOwnRenderable() {
		return false;
	}

}
