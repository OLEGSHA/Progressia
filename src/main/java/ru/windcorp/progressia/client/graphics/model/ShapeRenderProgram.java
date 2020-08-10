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

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import com.google.common.collect.ObjectArrays;

import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.backend.VertexBufferObject;
import ru.windcorp.progressia.client.graphics.backend.VertexBufferObject.BindTarget;
import ru.windcorp.progressia.client.graphics.backend.shaders.CombinedShader;
import ru.windcorp.progressia.client.graphics.backend.shaders.Program;
import ru.windcorp.progressia.client.graphics.backend.shaders.attributes.*;
import ru.windcorp.progressia.client.graphics.backend.shaders.uniforms.*;
import ru.windcorp.progressia.client.graphics.texture.Sprite;
import ru.windcorp.progressia.client.graphics.texture.Texture;

public class ShapeRenderProgram extends Program {
	
	private static final int DEFAULT_BYTES_PER_VERTEX =
			3 * Float.BYTES + // Position
			3 * Float.BYTES + // Color multiplier
			2 * Float.BYTES;  // Texture coordinates
	
	private static final String SHAPE_VERTEX_SHADER_RESOURCE =
			"Shape.vertex.glsl";
	private static final String SHAPE_FRAGMENT_SHADER_RESOURCE =
			"Shape.fragment.glsl";
	
	private static final String
			FINAL_TRANSFORM_UNIFORM_NAME   = "finalTransform",
			POSITIONS_ATTRIBUTE_NAME       = "inputPositions",
			COLOR_MULTIPLER_ATTRIBUTE_NAME = "inputColorMultiplier",
			TEXTURE_COORDS_ATTRIBUTE_NAME  = "inputTextureCoords",
			USE_TEXTURE_UNIFORM_NAME       = "useTexture",
			TEXTURE_SLOT_UNIFORM_NAME      = "textureSlot",
			TEXTURE_START_UNIFORM_NAME     = "textureStart",
			TEXTURE_SIZE_UNIFORM_NAME      = "textureSize";
	
	private final Uniform4Matrix finalTransformUniform;
	private final AttributeVertexArray positionsAttribute;
	private final AttributeVertexArray colorsAttribute;
	private final AttributeVertexArray textureCoordsAttribute;
	private final Uniform1Int useTextureUniform;
	private final Uniform1Int textureSlotUniform;
	private final Uniform2Float textureStartUniform;
	private final Uniform2Float textureSizeUniform;

	public ShapeRenderProgram(
			String[] vertexShaderResources,
			String[] fragmentShaderResources
	) {
		super(
				new CombinedShader(
						attachVertexShader(vertexShaderResources)
				),
				new CombinedShader(
						attachFragmentShader(fragmentShaderResources)
				)
		);
		
		this.finalTransformUniform = getUniform(FINAL_TRANSFORM_UNIFORM_NAME)
				.as4Matrix();
		
		this.positionsAttribute =
				getAttribute(POSITIONS_ATTRIBUTE_NAME).asVertexArray();
		
		this.colorsAttribute =
				getAttribute(COLOR_MULTIPLER_ATTRIBUTE_NAME).asVertexArray();
		
		this.textureCoordsAttribute =
				getAttribute(TEXTURE_COORDS_ATTRIBUTE_NAME).asVertexArray();
		
		this.useTextureUniform = getUniform(USE_TEXTURE_UNIFORM_NAME)
				.as1Int();
		
		this.textureSlotUniform = getUniform(TEXTURE_SLOT_UNIFORM_NAME)
				.as1Int();
		
		this.textureStartUniform = getUniform(TEXTURE_START_UNIFORM_NAME)
				.as2Float();
		
		this.textureSizeUniform = getUniform(TEXTURE_SIZE_UNIFORM_NAME)
				.as2Float();
	}

	private static String[] attachVertexShader(String[] others) {
		return ObjectArrays.concat(SHAPE_VERTEX_SHADER_RESOURCE, others);
	}
	
	private static String[] attachFragmentShader(String[] others) {
		return ObjectArrays.concat(SHAPE_FRAGMENT_SHADER_RESOURCE, others);
	}
	
	public void render(
			ShapeRenderHelper helper,
			Shape shape
	) {
		use();
		configure(helper);
		
		bindVertices(shape.getVerticesVbo());
		bindIndices(shape.getIndicesVbo());
		
		try {
			enableAttributes();
			for (Face face : shape.getFaces()) {
				renderFace(face);
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
	}

	protected int bindVertices(VertexBufferObject vertices) {
		int vertexStride = getBytesPerVertex();
		int offset = 0;
		
		positionsAttribute.set(
				3, GL11.GL_FLOAT, false, vertexStride, vertices,
				offset
		);
		offset += 3 * Float.BYTES;
		
		colorsAttribute.set(
				3, GL11.GL_FLOAT, false, vertexStride, vertices,
				offset
		);
		offset += 3 * Float.BYTES;
		
		textureCoordsAttribute.set(
				2, GL11.GL_FLOAT, false, vertexStride, vertices,
				offset
		);
		offset += 2 * Float.BYTES;
		
		return offset;
	}
	
	protected void bindIndices(VertexBufferObject indices) {
		indices.bind(BindTarget.ELEMENT_ARRAY);
	}

	protected void renderFace(Face face) {
		Texture texture = face.getTexture();
		
		if (texture != null) {
			Sprite sprite = texture.getSprite();
			
			sprite.getPrimitive().bind(0);
			textureSlotUniform.set(0);
			useTextureUniform.set(1);
			
			textureStartUniform.set(sprite.getStart());
			textureSizeUniform.set(sprite.getSize());
		} else {
			useTextureUniform.set(0);
		}
		
		GL11.glDrawElements(
				GL11.GL_TRIANGLES,
				face.getIndexCount(),
				GL11.GL_UNSIGNED_SHORT,
				face.getByteOffsetOfIndices()
		);
	}
	
	public int getBytesPerVertex() {
		return DEFAULT_BYTES_PER_VERTEX;
	}
	
	public void preprocess(Shape shape) {
		// To be overridden
	}
	
	public VertexBuilder getVertexBuilder() {
		return new SRPVertexBuilder();
	}
	
	public static interface VertexBuilder {
		VertexBuilder addVertex(
				float x, float y, float z,
				float r, float g, float b,
				float tx, float ty
		);
		
		VertexBuilder addVertex(
				Vec3 position,
				Vec3 colorMultiplier,
				Vec2 textureCoords
		);
		
		ByteBuffer assemble();
	}
	
	public static class SRPVertexBuilder implements VertexBuilder {
		
		private static class Vertex {
			final Vec3 position;
			final Vec3 colorMultiplier;
			final Vec2 textureCoords;
			
			Vertex(Vec3 position, Vec3 colorMultiplier, Vec2 textureCoords) {
				this.position = position;
				this.colorMultiplier = colorMultiplier;
				this.textureCoords = textureCoords;
			}
		}
		
		private final List<Vertex> vertices = new ArrayList<>();
		
		public VertexBuilder addVertex(
				float x, float y, float z,
				float r, float g, float b,
				float tx, float ty
		) {
			vertices.add(new Vertex(
					new Vec3(x, y, z),
					new Vec3(r, g, b),
					new Vec2(tx, ty)
			));
			
			return this;
		}
		
		public VertexBuilder addVertex(
				Vec3 position,
				Vec3 colorMultiplier,
				Vec2 textureCoords
		) {
			vertices.add(new Vertex(
					new Vec3(position),
					new Vec3(colorMultiplier),
					new Vec2(textureCoords)
			));
			
			return this;
		}
		
		public ByteBuffer assemble() {
			ByteBuffer result = BufferUtils.createByteBuffer(
					DEFAULT_BYTES_PER_VERTEX * vertices.size()
			);
			
			for (Vertex v : vertices) {
				result
					.putFloat(v.position.x)
					.putFloat(v.position.y)
					.putFloat(v.position.z)
					.putFloat(v.colorMultiplier.x)
					.putFloat(v.colorMultiplier.y)
					.putFloat(v.colorMultiplier.z)
					.putFloat(v.textureCoords.x)
					.putFloat(v.textureCoords.y);
			}
			
			result.flip();
			
			return result;
		}
		
	}

}
