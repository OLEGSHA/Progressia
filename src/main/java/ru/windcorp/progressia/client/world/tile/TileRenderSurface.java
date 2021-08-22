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

import java.util.function.Consumer;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.ShapePart;
import ru.windcorp.progressia.client.graphics.model.ShapeParts;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerSurface.TileOptimizedSurface;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.RelFace;

public abstract class TileRenderSurface extends TileRender implements TileOptimizedSurface {

	private final Texture texture;

	public TileRenderSurface(String id, Texture texture) {
		super(id);
		this.texture = texture;
	}
	
	public TileRenderSurface(String id) {
		this(id, null);
	}

	public Texture getTexture(RelFace blockFace) {
		return texture;
	}
	
	public Vec4 getColorMultiplier(RelFace blockFace) {
		return Colors.WHITE;
	}
	
	@Override
	public final void getShapeParts(
		DefaultChunkData chunk, Vec3i relBlockInChunk, RelFace blockFace,
		boolean inner,
		Consumer<ShapePart> output,
		Vec3 offset
	) {
		output.accept(createFace(chunk, relBlockInChunk, blockFace, inner, offset));
	}
	
	private ShapePart createFace(
		DefaultChunkData chunk, Vec3i blockInChunk, RelFace blockFace,
		boolean inner,
		Vec3 offset
	) {
		return ShapeParts.createBlockFace(
			WorldRenderProgram.getDefault(),
			getTexture(blockFace),
			getColorMultiplier(blockFace),
			offset,
			blockFace.resolve(AbsFace.POS_Z),
			inner
		);
	}

	@Override
	public Renderable createRenderable(DefaultChunkData chunk, Vec3i blockInChunk, RelFace blockFace) {
		return new Shape(
			Usage.STATIC,
			WorldRenderProgram.getDefault(),
			
			createFace(chunk, blockInChunk, blockFace, false, Vectors.ZERO_3),
			createFace(chunk, blockInChunk, blockFace, true,  Vectors.ZERO_3)
		);
	}

	@Override
	public boolean needsOwnRenderable() {
		return false;
	}

}
