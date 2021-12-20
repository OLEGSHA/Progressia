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

import java.nio.ShortBuffer;
import java.util.Objects;

import glm.mat._3.Mat3;
import glm.mat._4.Mat4;
import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram.VertexBuilder;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram.WRPVertexBuilder;
import ru.windcorp.progressia.common.util.StashingStack;
import ru.windcorp.progressia.common.util.VectorUtil;

public class ShapePrototype {
	
	private final ShapeRenderProgram program;
	
	private Texture texture;
	
	private final Vec3[] positions;
	private final Vec4[] colorMultipliers;
	private final Vec2[] textureCoords;
	private final Vec3[] forcedNormals;
	
	private ShortBuffer indices;
	private ShortBuffer flippedIndices = null;

	protected static final int TRANSFORM_STACK_SIZE = 64;
	private final StashingStack<Mat4> transformStack = new StashingStack<>(
		TRANSFORM_STACK_SIZE,
		Mat4::new
	);
	
	public ShapePrototype(
		ShapeRenderProgram program,
		Texture texture,
		Vec3[] positions,
		Vec4[] colorMultipliers,
		Vec2[] textureCoords,
		Vec3[] forcedNormals,
		ShortBuffer indices
	) {
		this.program = Objects.requireNonNull(program, "program");
		this.texture = texture;
		this.indices = Objects.requireNonNull(indices, "indices");

		Objects.requireNonNull(positions, "positions");
		Objects.requireNonNull(colorMultipliers, "colorMultipliers");
		Objects.requireNonNull(textureCoords, "textureCoords");
		
		if (forcedNormals != null && !(program instanceof WorldRenderProgram)) {
			throw new IllegalArgumentException("Cannot force normals on non-WorldRenderPrograms cuz javahorse stupiddd");
		}
		
		if (forcedNormals == null) {
			forcedNormals = new Vec3[positions.length];
		}
		
		this.positions = positions;
		this.colorMultipliers = colorMultipliers;
		this.textureCoords = textureCoords;
		this.forcedNormals = forcedNormals;
		
		if (positions.length != colorMultipliers.length) {
			throw new IllegalArgumentException("positions.length (" + positions.length + ") != colorMultipliers.length (" + colorMultipliers + ")");
		}
		
		if (positions.length != textureCoords.length) {
			throw new IllegalArgumentException("positions.length (" + positions.length + ") != textureCoords.length (" + textureCoords + ")");
		}
		
		if (positions.length != forcedNormals.length) {
			throw new IllegalArgumentException("positions.length (" + positions.length + ") != forcedNormals.length (" + forcedNormals + ")");
		}
		
		transformStack.push().identity();
	}
	
	public ShapePart build() {
		VertexBuilder builder = program.getVertexBuilder();

		Vec3 transformedPosition = new Vec3();
		
		for (int i = 0; i < positions.length; ++i) {
			
			transformedPosition.set(positions[i]);
			if (transformStack.getSize() > 1) {
				VectorUtil.applyMat4(transformedPosition, transformStack.getHead());
			}
			
			if (forcedNormals[i] != null && builder instanceof WRPVertexBuilder) {
				((WRPVertexBuilder) builder).addVertex(
					transformedPosition, colorMultipliers[i], textureCoords[i], forcedNormals[i]
				);
			} else {
				builder.addVertex(transformedPosition, colorMultipliers[i], textureCoords[i]);
			}
		}
		
		return new ShapePart(texture, builder.assemble(), indices);
	}
	
	public ShapePrototype apply(Mat4 transform) {
		for (Vec3 vector : positions) {
			VectorUtil.applyMat4(vector, transform);
		}
		return this;
	}
	
	public ShapePrototype apply(Mat3 transform) {
		for (Vec3 vector : positions) {
			transform.mul(vector);
		}
		return this;
	}
	
	public Mat4 push() {
		Mat4 previous = transformStack.getHead();
		return transformStack.push().set(previous);
	}
	
	public ShapePrototype pop() {
		transformStack.pop();
		return this;
	}
	
	public ShapePrototype setTexture(Texture texture) {
		this.texture = texture;
		return this;
	}
	
	public ShapePrototype deleteTexture() {
		this.texture = null;
		return this;
	}
	
	public ShapePrototype resetColorMultiplier() {
		for (Vec4 color : colorMultipliers) {
			color.set(Colors.WHITE);
		}
		return this;
	}
	
	public ShapePrototype addColorMultiplier(Vec4 color) {
		for (Vec4 c : colorMultipliers) {
			c.mul(color);
		}
		return this;
	}
	
	public ShapePrototype flip() {
		ShortBuffer tmp = indices;
		indices = flippedIndices;
		flippedIndices = tmp;
		
		if (indices == null) {
			int length = flippedIndices.limit();
			indices = ShortBuffer.allocate(length);
			for (int i = 0; i < length; ++i) {
				indices.put(i, flippedIndices.get(length - i - 1));
			}
		}
		
		return this;
	}
	
	public ShapePrototype makeDoubleSided() {
		int length = indices.limit();
		ShortBuffer newIndices = ShortBuffer.allocate(length * 2);
		
		for (int i = 0; i < length; ++i) {
			newIndices.put(i, indices.get(i));
		}
		for (int i = 0; i < length; ++i) {
			newIndices.put(i + length, indices.get(length - i - 1));
		}
		
		indices = flippedIndices = newIndices;
		
		return this;
	}
	
	public static ShapePrototype unitSquare(Texture texture, Vec4 color, ShapeRenderProgram program) {
		return new ShapePrototype(
			program,
			texture,
			new Vec3[] {
				new Vec3(0, 0, 0),
				new Vec3(0, 1, 0),
				new Vec3(1, 0, 0),
				new Vec3(1, 1, 0)
			},
			new Vec4[] {
				new Vec4(color),
				new Vec4(color),
				new Vec4(color),
				new Vec4(color)
			},
			new Vec2[] {
				new Vec2(0, 0),
				new Vec2(0, 1),
				new Vec2(1, 0),
				new Vec2(1, 1)
			},
			null,
			ShortBuffer.wrap(new short[] {3, 1, 0, 2, 3, 0})
		);
	}
	
	public static ShapePrototype unitSquare(Texture texture, Vec4 color) {
		return unitSquare(texture, color, WorldRenderProgram.getDefault());
	}
	
	public static ShapePrototype unitSquare(Texture texture) {
		return unitSquare(texture, Colors.WHITE, WorldRenderProgram.getDefault());
	}
	
	public static ShapePrototype unitSquare(Vec4 color) {
		return unitSquare(null, color, WorldRenderProgram.getDefault());
	}
	
	public static ShapePrototype unitSquare() {
		return unitSquare(null, Colors.WHITE, WorldRenderProgram.getDefault());
	}

}
