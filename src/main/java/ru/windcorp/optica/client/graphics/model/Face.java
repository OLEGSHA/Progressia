/*******************************************************************************
 * Optica
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
package ru.windcorp.optica.client.graphics.model;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;
import java.util.Objects;

import glm.vec._3.Vec3;
import ru.windcorp.optica.client.graphics.texture.Texture;

public class Face {
	
	private static final ShortBuffer GENERATE_SUCCESSIVE_LATER = null;
	
	private Shape shape = null;
	int locationOfIndices;
	int locationOfVertices;
	
	private Texture texture;
	
	ByteBuffer vertices;
	private boolean verticesUpdated = true;
	
	private ShortBuffer userIndices;
	private boolean userIndicesUpdated = true;
	
	public Face(
			Texture texture,
			ByteBuffer vertices,
			ShortBuffer indices
	) {
		setTexture(texture);
		setVertices(vertices);
		setIndices(indices);
	}
	
	public Face(
			Texture texture,
			ByteBuffer vertices
	) {
		this(texture, vertices, null);
	}
	
	void setShape(Shape shape) {
		this.shape = shape;
		
		checkVertices();
		checkIndices();
	}
	
	void computeNormals() {
		Vec3 a = new Vec3();
		Vec3 b = new Vec3();
		Vec3 c = new Vec3();
		Vec3 normal = new Vec3();
		
		for (int i = 0; i < getIndexCount(); i += 3) {
			int indexA = getIndex(i + 0);
			int indexB = getIndex(i + 1);
			int indexC = getIndex(i + 2);
			
			loadVertexPosition(indexA, a);
			loadVertexPosition(indexB, b);
			loadVertexPosition(indexC, c);
			
			computeOneNormal(a, b, c, normal);
			
			saveVertexNormal(indexA, normal);
			saveVertexNormal(indexB, normal);
			saveVertexNormal(indexC, normal);
		}
	}
	
	private void computeOneNormal(
			Vec3 a, Vec3 b, Vec3 c,
			Vec3 normal
	) {
		b.sub(a);
		c.sub(a);
		b.cross(c, normal);
		normal.normalize();
	}

	private void checkVertices() {
		if (vertices.remaining() % getBytesPerVertex() != 0) {
			throw new IllegalArgumentException(
					"Invalid vertex buffer: " +
					(vertices.remaining() % getBytesPerVertex()) +
					" extra bytes after last vertex"
			);
		}
	}

	private void checkIndices() {
		if (userIndices != GENERATE_SUCCESSIVE_LATER) {
			if (userIndices.remaining() % 3 != 0) {
				throw new IllegalArgumentException(
						"Invalid vertex indices: " +
						(userIndices.remaining() % 3) +
						" extra indices after last triangle"
				);
			}
			
			userIndices.mark();
			int vertexCount = getVertexCount();
			
			while (userIndices.hasRemaining()) {
				short index = userIndices.get();
				if (index < 0 || index >= vertexCount) {
					throw new IllegalArgumentException(
							"Invalid vertex index " + index +
							" (" + vertexCount + " vertices available)"
					);
				}
			}
			
			userIndices.reset();
		} else {
			if (getVertexCount() % 3 != 0) {
				throw new IllegalArgumentException(
						"Invalid vertices: " +
						(getVertexCount() % 3) +
						" extra indices after last triangle " +
						"(indices are automatic)"
				);
			}
		}
	}
	
	boolean needsVerticesUpdate() {
		return verticesUpdated;
	}
	
	public void markForIndexUpdate() {
		if (shape != null) checkIndices();
		markShapeForReassembly();
		userIndicesUpdated = true;
	}

	boolean needsIndicesUpdate() {
		return userIndicesUpdated;
	}
	
	void resetUpdateFlags() {
		verticesUpdated = false;
		userIndicesUpdated = false;
	}
	
	private void markShapeForReassembly() {
		if (shape != null) {
			shape.markForReassembly();
		}
	}
	
	public int getVertexCount() {
		return vertices.remaining() / getBytesPerVertex();
	}
	
	private int getBytesPerVertex() {
		return shape.getProgram().getBytesPerVertex();
	}
	
	public ByteBuffer getVertices() {
		return vertices;
	}
	
	private void loadVertexPosition(int index, Vec3 result) {
		int offset = vertices.position() + index * getBytesPerVertex();
		
		result.set(
				vertices.getFloat(offset + 0 * Float.BYTES),
				vertices.getFloat(offset + 1 * Float.BYTES),
				vertices.getFloat(offset + 2 * Float.BYTES)
		);
	}
	
	private void saveVertexNormal(int index, Vec3 normal) {
		int offset = vertices.position() + index * getBytesPerVertex() + (
				3 * Float.BYTES +
				3 * Float.BYTES +
				2 * Float.BYTES
		);
		
		vertices.putFloat(offset + 0 * Float.BYTES, normal.x);
		vertices.putFloat(offset + 1 * Float.BYTES, normal.y);
		vertices.putFloat(offset + 2 * Float.BYTES, normal.z);
		
		verticesUpdated = true;
	}
	
	public Face setVertices(ByteBuffer vertices) {
		this.vertices = Objects.requireNonNull(vertices, "vertices");
		markShapeForReassembly();
		this.verticesUpdated = true;
		
		if (shape != null) checkVertices();
		
		return this;
	}

	int getLocationOfVertices() {
		return locationOfVertices;
	}
	
	int getByteOffsetOfVertices() {
		return locationOfVertices;
	}
	
	public ShortBuffer getIndices() {
		if (userIndices == GENERATE_SUCCESSIVE_LATER) {
			userIndices = generateSuccessiveIndices(0);
		}
		
		return userIndices;
	}
	
	public int getIndex(int i) {
		if (userIndices == GENERATE_SUCCESSIVE_LATER) {
			return i;
		} else {
			ShortBuffer indices = getIndicesOrNull();
			return indices.get(indices.position() + i);
		}
	}
	
	ShortBuffer getIndicesOrNull() {
		if (userIndices == GENERATE_SUCCESSIVE_LATER) {
			return null;
		}
		
		return userIndices;
	}
	
	public int getIndexCount() {
		if (userIndices == GENERATE_SUCCESSIVE_LATER) {
			return getVertexCount();
		}
		
		return userIndices.remaining();
	}
	
	public Face setIndices(ShortBuffer indices) {
		if (indices == null) {
			indices = GENERATE_SUCCESSIVE_LATER;
		}
		
		this.userIndices = indices;
		markForIndexUpdate();
		
		if (shape != null) checkIndices();
		
		return this;
	}
	
	private ShortBuffer generateSuccessiveIndices(int offset) {
		int vertexCount = getVertexCount();
		ShortBuffer result = ShortBuffer.allocate(vertexCount);
		
		for (short vertex = 0; vertex < vertexCount; ++vertex) {
			result.put((short) (vertex + offset));
		}
		
		result.flip();
		return result;
	}
	
	int getLocationOfIndices() {
		return locationOfIndices;
	}
	
	int getByteOffsetOfIndices() {
		return locationOfIndices * Short.BYTES;
	}
	
	public Texture getTexture() {
		return texture;
	}
	
	public void setTexture(Texture texture) {
		this.texture = Objects.requireNonNull(texture, "texture");
	}

}
