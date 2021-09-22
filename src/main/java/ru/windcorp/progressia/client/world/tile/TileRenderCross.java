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
package ru.windcorp.progressia.client.world.tile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import glm.mat._3.Mat3;
import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapePart;
import ru.windcorp.progressia.client.graphics.model.ShapeParts;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerSimple.TileOptimizedCustom;
import ru.windcorp.progressia.common.util.Matrices;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.AxisRotations;
import ru.windcorp.progressia.common.world.rels.RelFace;

public class TileRenderCross extends TileRender implements TileOptimizedCustom {

	private static final float SQRT_2_OVER_2 = (float) Math.sqrt(2) / 2;
	private static final float[] ONE_AND_NEGATIVE_ONE = new float[] { 1, -1 };

	private final Texture texture;
	private final float width;

	public TileRenderCross(String id, Texture texture, boolean allowStretching) {
		super(id);
		this.texture = texture;
		this.width = allowStretching ? 1 : SQRT_2_OVER_2;
	}

	public Texture getTexture(RelFace blockFace) {
		return texture;
	}

	public Vec4 getColorMultiplier(RelFace blockFace) {
		return Colors.WHITE;
	}

	@Override
	public void getShapeParts(
		DefaultChunkData chunk,
		Vec3i bic,
		RelFace blockFace,
		Consumer<ShapePart> output
	) {
		Mat4 transform = Matrices.grab4();
		Vec3 origin = Vectors.grab3();
		Vec3 width = Vectors.grab3();
		Vec3 height = Vectors.grab3();

		Mat3 resolutionMatrix = AxisRotations.getResolutionMatrix3(blockFace.resolve(AbsFace.POS_Z));

		Vec4 color = getColorMultiplier(blockFace);
		Texture texture = getTexture(blockFace);
		float originOffset = (1 - this.width) / 2;

		WorldRenderProgram program = WorldRenderProgram.getDefault();

		for (int i = 0; getTransform(chunk, bic, blockFace, i, transform); i++) {

			for (float flip : ONE_AND_NEGATIVE_ONE) {
				origin.set(flip * (originOffset - 0.5f), originOffset - 0.5f, 0);
				width.set(flip * this.width, this.width, 0);
				height.set(0, 0, 1);

				VectorUtil.applyMat4(origin, transform);
				VectorUtil.rotateOnly(width, transform);
				VectorUtil.rotateOnly(height, transform);

				origin.z += 1 - 0.5f;

				if (blockFace != RelFace.UP) {
					resolutionMatrix.mul(origin);
					resolutionMatrix.mul(width);
					resolutionMatrix.mul(height);
				}

				origin.add(bic.x, bic.y, bic.z);

				output.accept(
					ShapeParts.createRectangle(
						program,
						texture,
						color,
						origin,
						width,
						height,
						false,
						new Vec3(0, 0, 1)
					)
				);
				output.accept(
					ShapeParts.createRectangle(
						program,
						texture,
						color,
						origin,
						width,
						height,
						true,
						new Vec3(0, 0, 1)
					)
				);
			}

		}

		Matrices.release(transform);
		Vectors.release(origin);
		Vectors.release(width);
		Vectors.release(height);
	}

	protected boolean getTransform(
		DefaultChunkData chunk,
		Vec3i relBlockInChunk,
		RelFace blockFace,
		int count,
		Mat4 output
	) {
		output.identity();
		return count == 0;
	}

	@Override
	public Renderable createRenderable(DefaultChunkData chunk, Vec3i blockInChunk, RelFace blockFace) {
		Collection<ShapePart> parts = new ArrayList<>(4);

		getShapeParts(chunk, blockInChunk, blockFace, parts::add);

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
