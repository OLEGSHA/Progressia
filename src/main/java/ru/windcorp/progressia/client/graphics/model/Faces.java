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

public class Faces {
	
	private Faces() {}
	
	public static Face createRectangle(
			ShapeRenderProgram program,
			Texture texture,
			Vec3 colorMultiplier,
			Vec3 origin,
			Vec3 width,
			Vec3 height
	) {
		VertexBuilder builder = program.getVertexBuilder();
		
		Vec3 pos = new Vec3();
		Vec2 texCoords = new Vec2();
		
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
		
		return new Face(
				texture,
				builder.assemble(),
				ShortBuffer.wrap(new short[] {
						3, 1, 0,
						2, 3, 0
				})
		);
	}
	
	public static Face createBlockFace(
			ShapeRenderProgram program,
			Texture texture,
			Vec3 colorMultiplier,
			Vec3 blockCenter,
			BlockFace face
	) {
		switch (face) {
		case TOP:
			return createRectangle(
					program,
					texture, colorMultiplier,
					blockCenter.add(-0.5f, +0.5f, +0.5f),
					new Vec3( 0, -1,  0),
					new Vec3(+1,  0,  0)
			);
		case BOTTOM:
			return createRectangle(
					program,
					texture, colorMultiplier,
					blockCenter.add(-0.5f, -0.5f, -0.5f),
					new Vec3( 0, +1,  0),
					new Vec3(+1,  0,  0)
			);
		case NORTH:
			return createRectangle(
					program,
					texture, colorMultiplier,
					blockCenter.add(+0.5f, -0.5f, -0.5f),
					new Vec3( 0, +1,  0),
					new Vec3( 0,  0, +1)
			);
		case SOUTH:
			return createRectangle(
					program,
					texture, colorMultiplier,
					blockCenter.add(-0.5f, +0.5f, -0.5f),
					new Vec3( 0, -1,  0),
					new Vec3( 0,  0, +1)
			);
		case EAST:
			return createRectangle(
					program,
					texture, colorMultiplier,
					blockCenter.add(-0.5f, -0.5f, -0.5f),
					new Vec3(+1,  0,  0),
					new Vec3( 0,  0, +1)
			);
		case WEST:
			return createRectangle(
					program,
					texture, colorMultiplier,
					blockCenter.add(+0.5f, +0.5f, -0.5f),
					new Vec3(-1,  0,  0),
					new Vec3( 0,  0, +1)
			);
		default:
			throw new NullPointerException("face");
		}
	}

}
