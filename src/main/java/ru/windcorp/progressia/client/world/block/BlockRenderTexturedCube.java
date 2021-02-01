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
 
package ru.windcorp.progressia.client.world.block;

import static ru.windcorp.progressia.common.world.block.AbsFace.*;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.Face;
import ru.windcorp.progressia.client.graphics.model.Faces;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerSurface.BlockOptimizedSurface;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.block.AbsFace;

public abstract class BlockRenderTexturedCube
	extends BlockRender
	implements BlockOptimizedSurface {

	private final Map<AbsFace, Texture> textures = new HashMap<>();

	public BlockRenderTexturedCube(
		String id,
		Texture posZTexture,
		Texture negZTexture,
		Texture posXTexture,
		Texture negXTexture,
		Texture negYTexture,
		Texture posYTexture
	) {
		super(id);

		textures.put(POS_Z, posZTexture);
		textures.put(NEG_Z, negZTexture);
		textures.put(POS_X, posXTexture);
		textures.put(NEG_X, negXTexture);
		textures.put(NEG_Y, negYTexture);
		textures.put(POS_Y, posYTexture);
	}

	public Texture getTexture(AbsFace blockFace) {
		return textures.get(blockFace);
	}
	
	public Vec4 getColorMultiplier(AbsFace blockFace) {
		return Colors.WHITE;
	}

	@Override
	public final void getFaces(
		ChunkData chunk, Vec3i blockInChunk, AbsFace blockFace,
		boolean inner,
		Consumer<Face> output,
		Vec3 offset
	) {
		output.accept(createFace(chunk, blockInChunk, blockFace, inner, offset));
	}
	
	private Face createFace(
		ChunkData chunk, Vec3i blockInChunk, AbsFace blockFace,
		boolean inner,
		Vec3 offset
	) {
		return Faces.createBlockFace(
			WorldRenderProgram.getDefault(),
			getTexture(blockFace),
			getColorMultiplier(blockFace),
			offset,
			blockFace,
			inner
		);
	}

	@Override
	public Renderable createRenderable(ChunkData chunk, Vec3i blockInChunk) {
		boolean opaque = isBlockOpaque();
		
		Face[] faces = new Face[BLOCK_FACE_COUNT + (opaque ? BLOCK_FACE_COUNT : 0)];
		
		for (int i = 0; i < BLOCK_FACE_COUNT; ++i) {
			faces[i] = createFace(chunk, blockInChunk, AbsFace.getFaces().get(i), false, Vectors.ZERO_3);
		}
		
		if (!opaque) {
			for (int i = 0; i < BLOCK_FACE_COUNT; ++i) {
				faces[i + BLOCK_FACE_COUNT] = createFace(chunk, blockInChunk, AbsFace.getFaces().get(i), true, Vectors.ZERO_3);
			}
		}
		
		return new Shape(Usage.STATIC, WorldRenderProgram.getDefault(), faces);
	}

	@Override
	public boolean needsOwnRenderable() {
		return false;
	}

}
