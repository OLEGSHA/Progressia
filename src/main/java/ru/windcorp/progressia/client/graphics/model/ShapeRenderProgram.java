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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.ObjectArrays;

import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.backend.VertexBufferObject;
import ru.windcorp.progressia.client.graphics.backend.VertexBufferObject.BindTarget;
import ru.windcorp.progressia.client.graphics.backend.shaders.CombinedShader;
import ru.windcorp.progressia.client.graphics.backend.shaders.Program;
import ru.windcorp.progressia.client.graphics.backend.shaders.attributes.*;
import ru.windcorp.progressia.client.graphics.backend.shaders.uniforms.*;
import ru.windcorp.progressia.client.graphics.texture.Sprite;
import ru.windcorp.progressia.client.graphics.texture.TexturePrimitive;
import ru.windcorp.progressia.common.util.Vectors;

public class ShapeRenderProgram extends Program {

	private static final int DEFAULT_BYTES_PER_VERTEX = 3 * Float.BYTES + // Position
			4 * Float.BYTES + // Color multiplier
			2 * Float.BYTES; // Texture coordinates

	private static final String SHAPE_VERTEX_SHADER_RESOURCE = "Shape.vertex.glsl";
	private static final String SHAPE_FRAGMENT_SHADER_RESOURCE = "Shape.fragment.glsl";

	private static final String FINAL_TRANSFORM_UNIFORM_NAME = "finalTransform",
			POSITIONS_ATTRIBUTE_NAME = "inputPositions",
			UNIFORM_COLOR_MULTIPLER_ATTRIBUTE_NAME = "uniformColorMultiplier",
			ATTRIBUTE_COLOR_MULTIPLER_ATTRIBUTE_NAME = "inputColorMultiplier",
			TEXTURE_COORDS_ATTRIBUTE_NAME = "inputTextureCoords", USE_TEXTURE_UNIFORM_NAME = "useTexture",
			TEXTURE_SLOT_UNIFORM_NAME = "textureSlot";

	private final Uniform4Matrix finalTransformUniform;
	private final AttributeVertexArray positionsAttribute;
	private final Uniform4Float colorsUniform;
	private final AttributeVertexArray colorsAttribute;
	private final AttributeVertexArray textureCoordsAttribute;
	private final Uniform1Int useTextureUniform;
	private final Uniform1Int textureSlotUniform;

	public ShapeRenderProgram(String[] vertexShaderResources, String[] fragmentShaderResources) {
		super(new CombinedShader(attachVertexShader(vertexShaderResources)),
				new CombinedShader(attachFragmentShader(fragmentShaderResources)));

		this.finalTransformUniform = getUniform(FINAL_TRANSFORM_UNIFORM_NAME).as4Matrix();

		this.positionsAttribute = getAttribute(POSITIONS_ATTRIBUTE_NAME).asVertexArray();

		this.colorsUniform = getUniform(UNIFORM_COLOR_MULTIPLER_ATTRIBUTE_NAME).as4Float();

		this.colorsAttribute = getAttribute(ATTRIBUTE_COLOR_MULTIPLER_ATTRIBUTE_NAME).asVertexArray();

		this.textureCoordsAttribute = getAttribute(TEXTURE_COORDS_ATTRIBUTE_NAME).asVertexArray();

		this.useTextureUniform = getUniform(USE_TEXTURE_UNIFORM_NAME).as1Int();

		this.textureSlotUniform = getUniform(TEXTURE_SLOT_UNIFORM_NAME).as1Int();
	}

	private static String[] attachVertexShader(String[] others) {
		return ObjectArrays.concat(SHAPE_VERTEX_SHADER_RESOURCE, others);
	}

	private static String[] attachFragmentShader(String[] others) {
		return ObjectArrays.concat(SHAPE_FRAGMENT_SHADER_RESOURCE, others);
	}

	public void render(ShapeRenderHelper helper, Shape shape) {
		use();
		configure(helper);

		bindVertices(shape.getVerticesVbo());
		bindIndices(shape.getIndicesVbo());

		try {
			enableAttributes();
			for (FaceGroup group : shape.getGroups()) {
				renderFaceGroup(group);
			}
		} finally {
			disableAttributes();
		}
	}

	protected void enableAttributes() {
		positionsAttribute.enable();
		colorsAttribute.enable();
		textureCoordsAttribute.enable();
	}

	protected void disableAttributes() {
		positionsAttribute.disable();
		colorsAttribute.disable();
		textureCoordsAttribute.disable();
	}

	protected void configure(ShapeRenderHelper helper) {
		finalTransformUniform.set(helper.getFinalTransform());
		colorsUniform.set(helper.getFinalColorMultiplier());
	}

	protected int bindVertices(VertexBufferObject vertices) {
		int vertexStride = getBytesPerVertex();
		int offset = 0;

		positionsAttribute.set(3, GL11.GL_FLOAT, false, vertexStride, vertices, offset);
		offset += 3 * Float.BYTES;

		colorsAttribute.set(4, GL11.GL_FLOAT, false, vertexStride, vertices, offset);
		offset += 4 * Float.BYTES;

		textureCoordsAttribute.set(2, GL11.GL_FLOAT, false, vertexStride, vertices, offset);
		offset += 2 * Float.BYTES;

		return offset;
	}

	protected void bindIndices(VertexBufferObject indices) {
		indices.bind(BindTarget.ELEMENT_ARRAY);
	}

	protected void renderFaceGroup(FaceGroup group) {
		TexturePrimitive texture = group.getTexture();

		if (texture != null) {
			texture.bind(0);
			textureSlotUniform.set(0);
			useTextureUniform.set(1);
		} else {
			useTextureUniform.set(0);
		}

		GL11.glDrawElements(GL11.GL_TRIANGLES, group.getIndexCount(), GL11.GL_UNSIGNED_SHORT,
				group.getByteOffsetOfIndices());
	}

	public int getBytesPerVertex() {
		return DEFAULT_BYTES_PER_VERTEX;
	}

	public void preprocess(Shape shape) {
		for (Face face : shape.getFaces()) {
			applySprites(face);
		}
	}

	private void applySprites(Face face) {
		if (face.getTexture() == null)
			return;

		Vec2 v = Vectors.grab2();
		ByteBuffer vertices = face.getVertices();
		Sprite sprite = face.getTexture().getSprite();

		for (int i = 0; i < face.getVertexCount(); i++) {
			int offset = vertices.position() + i * getBytesPerVertex() + (3 * Float.BYTES + 4 * Float.BYTES);

			v.set(vertices.getFloat(offset + 0 * Float.BYTES), vertices.getFloat(offset + 1 * Float.BYTES));

			v.mul(sprite.getSize()).add(sprite.getStart());

			vertices.putFloat(offset + 0 * Float.BYTES, v.x);
			vertices.putFloat(offset + 1 * Float.BYTES, v.y);
		}

		face.markForVertexUpdate();

		Vectors.release(v);
	}

	public VertexBuilder getVertexBuilder() {
		return new SRPVertexBuilder();
	}

	public static interface VertexBuilder {
		VertexBuilder addVertex(float x, float y, float z, float r, float g, float b, float tx, float ty);

		VertexBuilder addVertex(float x, float y, float z, float r, float g, float b, float a, float tx, float ty);

		VertexBuilder addVertex(Vec3 position, Vec4 colorMultiplier, Vec2 textureCoords);

		ByteBuffer assemble();
	}

	public static class SRPVertexBuilder implements VertexBuilder {

		private static class Vertex {
			final Vec3 position;
			final Vec4 colorMultiplier;
			final Vec2 textureCoords;

			Vertex(Vec3 position, Vec4 colorMultiplier, Vec2 textureCoords) {
				this.position = position;
				this.colorMultiplier = colorMultiplier;
				this.textureCoords = textureCoords;
			}
		}

		private final List<Vertex> vertices = new ArrayList<>();

		@Override
		public VertexBuilder addVertex(float x, float y, float z, float r, float g, float b, float a, float tx,
				float ty) {
			vertices.add(new Vertex(new Vec3(x, y, z), new Vec4(r, g, b, a), new Vec2(tx, ty)));

			return this;
		}

		@Override
		public VertexBuilder addVertex(float x, float y, float z, float r, float g, float b, float tx, float ty) {
			vertices.add(new Vertex(new Vec3(x, y, z), new Vec4(r, g, b, 1f), new Vec2(tx, ty)));

			return this;
		}

		@Override
		public VertexBuilder addVertex(Vec3 position, Vec4 colorMultiplier, Vec2 textureCoords) {
			vertices.add(new Vertex(new Vec3(position), new Vec4(colorMultiplier), new Vec2(textureCoords)));

			return this;
		}

		@Override
		public ByteBuffer assemble() {
			ByteBuffer result = BufferUtils.createByteBuffer(DEFAULT_BYTES_PER_VERTEX * vertices.size());

			for (Vertex v : vertices) {
				result.putFloat(v.position.x).putFloat(v.position.y).putFloat(v.position.z)
						.putFloat(v.colorMultiplier.x).putFloat(v.colorMultiplier.y).putFloat(v.colorMultiplier.z)
						.putFloat(v.colorMultiplier.w).putFloat(v.textureCoords.x).putFloat(v.textureCoords.y);
			}

			result.flip();

			return result;
		}

	}

}
