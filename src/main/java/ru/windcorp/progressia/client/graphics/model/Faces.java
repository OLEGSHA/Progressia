/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.client.graphics.model;

import java.nio.ShortBuffer;

import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram.VertexBuilder;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.common.block.BlockFace;
import ru.windcorp.progressia.common.util.Vectors;

public class Faces {
	
	private Faces() {}
	
	public static Face createRectangle(
			ShapeRenderProgram program,
			Texture texture,
			Vec3 colorMultiplier,
			Vec3 origin,
			Vec3 width,
			Vec3 height,
			boolean flip
	) {
		VertexBuilder builder = program.getVertexBuilder();
		
		Vec3 pos = Vectors.grab3();
		Vec2 texCoords = Vectors.grab2();
		
		try {
		
			builder.addVertex(
					origin,
					colorMultiplier,
					texCoords.set(0, 0)
			).addVertex(
					pos.set(origin).add(height),
					colorMultiplier,
					texCoords.set(0, 1)
			).addVertex(
					pos.set(origin).add(width),
					colorMultiplier,
					texCoords.set(1, 0)
			).addVertex(
					pos.add(height),
					colorMultiplier,
					texCoords.set(1, 1)
			);
			
			ShortBuffer buffer = flip ? ShortBuffer.wrap(new short[] {
					0, 1, 3,
					0, 3, 2
			}) : ShortBuffer.wrap(new short[] {
					3, 1, 0,
					2, 3, 0
			});
			
			return new Face(
					texture,
					builder.assemble(),
					buffer
			);
		
		} finally {
			Vectors.release(pos);
			Vectors.release(texCoords);
		}
	}
	
	public static Face createBlockFace(
			ShapeRenderProgram program,
			Texture texture,
			Vec3 colorMultiplier,
			Vec3 blockCenter,
			BlockFace face,
			boolean inner
	) {
		BlockFaceVectors vectors = BlockFaceVectors.get(inner);
		
		Vec3 origin =
				Vectors.grab3().set(blockCenter).add(vectors.getOrigin(face));
		try {
			
			Vec3 width = vectors.getWidth(face);
			Vec3 height = vectors.getHeight(face);
			
			return createRectangle(
					program, texture, colorMultiplier,
					origin, width, height,
					inner
			);
			
		} finally {
			Vectors.release(origin);
		}
	}

}
