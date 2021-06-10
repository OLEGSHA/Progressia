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

package ru.windcorp.progressia.client.graphics.world;

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
import ru.windcorp.progressia.client.graphics.backend.shaders.attributes.*;
import ru.windcorp.progressia.client.graphics.backend.shaders.uniforms.*;
import ru.windcorp.progressia.client.graphics.model.Face;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram;
import ru.windcorp.progressia.common.util.Vectors;

public class WorldRenderProgram extends ShapeRenderProgram {

	private static WorldRenderProgram def = null;

	public static void init() {
		def = new WorldRenderProgram(new String[] { "WorldDefault.vertex.glsl" },
				new String[] { "WorldDefault.fragment.glsl" });
	}

	public static WorldRenderProgram getDefault() {
		return def;
	}

	private static final int DEFAULT_BYTES_PER_VERTEX = 3 * Float.BYTES + // Position
			4 * Float.BYTES + // Color multiplier
			2 * Float.BYTES + // Texture coordinates
			3 * Float.BYTES; // Normals

	private static final String WORLD_VERTEX_SHADER_RESOURCE = "World.vertex.glsl";
	private static final String WORLD_FRAGMENT_SHADER_RESOURCE = "World.fragment.glsl";

	private static final String WORLD_TRANSFORM_UNIFORM_NAME = "worldTransform",
			NORMALS_ATTRIBUTE_NAME = "inputNormals";

	private final Uniform4Matrix worldTransformUniform;
	private final AttributeVertexArray normalsAttribute;

	public WorldRenderProgram(String[] vertexShaderResources, String[] fragmentShaderResources) {
		super(attachVertexShader(vertexShaderResources), attachFragmentShader(fragmentShaderResources));

		this.worldTransformUniform = getUniform(WORLD_TRANSFORM_UNIFORM_NAME).as4Matrix();

		this.normalsAttribute = getAttribute(NORMALS_ATTRIBUTE_NAME).asVertexArray();
	}

	private static String[] attachVertexShader(String[] others) {
		return ObjectArrays.concat(WORLD_VERTEX_SHADER_RESOURCE, others);
	}

	private static String[] attachFragmentShader(String[] others) {
		return ObjectArrays.concat(WORLD_FRAGMENT_SHADER_RESOURCE, others);
	}

	@Override
	protected void configure(ShapeRenderHelper helper) {
		super.configure(helper);
		worldTransformUniform.set(helper.getTransform());
	}

	@Override
	protected int bindVertices(VertexBufferObject vertices) {
		int vertexStride = getBytesPerVertex();
		int offset = super.bindVertices(vertices);

		normalsAttribute.set(3, GL11.GL_FLOAT, false, vertexStride, vertices, offset);
		offset += 3 * Float.BYTES;

		return offset;
	}

	@Override
	protected void enableAttributes() {
		super.enableAttributes();
		normalsAttribute.enable();
	}

	@Override
	protected void disableAttributes() {
		super.disableAttributes();
		normalsAttribute.disable();
	}

	@Override
	public int getBytesPerVertex() {
		return super.getBytesPerVertex() + 3 * Float.BYTES; // Normals
	}

	@Override
	public void preprocess(Shape shape) {
		super.preprocess(shape);

		for (Face face : shape.getFaces()) {
			computeNormals(face);
		}
	}

	private void computeNormals(Face face) {
		Vec3 a = Vectors.grab3();
		Vec3 b = Vectors.grab3();
		Vec3 c = Vectors.grab3();
		Vec3 normal = Vectors.grab3();

		for (int i = 0; i < face.getIndexCount(); i += 3) {
			int indexA = face.getIndex(i + 0);
			int indexB = face.getIndex(i + 1);
			int indexC = face.getIndex(i + 2);

			loadVertexPosition(face, indexA, a);
			loadVertexPosition(face, indexB, b);
			loadVertexPosition(face, indexC, c);

			computeOneNormal(a, b, c, normal);

			saveVertexNormal(face, indexA, normal);
			saveVertexNormal(face, indexB, normal);
			saveVertexNormal(face, indexC, normal);
		}

		Vectors.release(a);
		Vectors.release(b);
		Vectors.release(c);
		Vectors.release(normal);
	}

	private void computeOneNormal(Vec3 a, Vec3 b, Vec3 c, Vec3 normal) {
		b.sub(a);
		c.sub(a);
		b.cross(c, normal);
		normal.normalize();
	}

	private void loadVertexPosition(Face face, int index, Vec3 result) {
		ByteBuffer vertices = face.getVertices();
		int offset = vertices.position() + index * getBytesPerVertex();

		result.set(vertices.getFloat(offset + 0 * Float.BYTES), vertices.getFloat(offset + 1 * Float.BYTES),
				vertices.getFloat(offset + 2 * Float.BYTES));
	}

	private void saveVertexNormal(Face face, int index, Vec3 normal) {
		ByteBuffer vertices = face.getVertices();
		int offset = vertices.position() + index * getBytesPerVertex()
				+ (3 * Float.BYTES + 4 * Float.BYTES + 2 * Float.BYTES);

		vertices.putFloat(offset + 0 * Float.BYTES, normal.x);
		vertices.putFloat(offset + 1 * Float.BYTES, normal.y);
		vertices.putFloat(offset + 2 * Float.BYTES, normal.z);

		face.markForVertexUpdate();
	}

	@Override
	public VertexBuilder getVertexBuilder() {
		return new WRPVertexBuilder();
	}

	private static class WRPVertexBuilder implements VertexBuilder {
		// TODO Throw VertexBuilders out the window and rewrite completely.
		// I want to _extend_ VBs, not re-implement them for children of SRP

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
		public VertexBuilder addVertex(float x, float y, float z, float r, float g, float b, float tx, float ty) {
			vertices.add(new Vertex(new Vec3(x, y, z), new Vec4(r, g, b, 1), new Vec2(tx, ty)));

			return this;
		}

		@Override
		public VertexBuilder addVertex(float x, float y, float z, float r, float g, float b, float a, float tx,
				float ty) {
			vertices.add(new Vertex(new Vec3(x, y, z), new Vec4(r, g, b, a), new Vec2(tx, ty)));

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
						.putFloat(v.colorMultiplier.w).putFloat(v.textureCoords.x).putFloat(v.textureCoords.y)
						.putFloat(Float.NaN).putFloat(Float.NaN).putFloat(Float.NaN);
			}

			result.flip();

			return result;
		}

	}

}
