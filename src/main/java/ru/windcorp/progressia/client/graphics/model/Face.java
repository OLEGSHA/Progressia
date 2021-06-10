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
import java.nio.ShortBuffer;
import java.util.Objects;

import ru.windcorp.progressia.client.graphics.texture.Texture;

public class Face implements Comparable<Face> {

	private static final ShortBuffer GENERATE_SUCCESSIVE_LATER = null;

	private Shape shape = null;
	int locationOfIndices;
	int locationOfVertices;

	private Texture texture;

	ByteBuffer vertices;
	private boolean verticesUpdated = true;

	private ShortBuffer userIndices;
	private boolean userIndicesUpdated = true;

	public Face(Texture texture, ByteBuffer vertices, ShortBuffer indices) {
		setTexture(texture);
		setVertices(vertices);
		setIndices(indices);
	}

	public Face(Texture texture, ByteBuffer vertices) {
		this(texture, vertices, null);
	}

	void setShape(Shape shape) {
		this.shape = shape;

		checkVertices();
		checkIndices();
	}

	private void checkVertices() {
		if (vertices.remaining() % getBytesPerVertex() != 0) {
			throw new IllegalArgumentException("Invalid vertex buffer: " + (vertices.remaining() % getBytesPerVertex())
					+ " extra bytes after last vertex");
		}
	}

	private void checkIndices() {
		if (userIndices != GENERATE_SUCCESSIVE_LATER) {
			if (userIndices.remaining() % 3 != 0) {
				throw new IllegalArgumentException("Invalid vertex indices: " + (userIndices.remaining() % 3)
						+ " extra indices after last triangle");
			}

			userIndices.mark();
			int vertexCount = getVertexCount();

			while (userIndices.hasRemaining()) {
				short index = userIndices.get();
				if (index < 0 || index >= vertexCount) {
					throw new IllegalArgumentException(
							"Invalid vertex index " + index + " (" + vertexCount + " vertices available)");
				}
			}

			userIndices.reset();
		} else {
			if (getVertexCount() % 3 != 0) {
				throw new IllegalArgumentException("Invalid vertices: " + (getVertexCount() % 3)
						+ " extra indices after last triangle " + "(indices are automatic)");
			}
		}
	}

	public void markForVertexUpdate() {
		if (shape != null)
			checkVertices();
		markShapeForReassembly();
		verticesUpdated = true;
	}

	boolean needsVerticesUpdate() {
		return verticesUpdated;
	}

	public void markForIndexUpdate() {
		if (shape != null)
			checkIndices();
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

	public Face setVertices(ByteBuffer vertices) {
		this.vertices = Objects.requireNonNull(vertices, "vertices");
		markForVertexUpdate();
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

		if (shape != null)
			checkIndices();

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
		this.texture = texture;
	}

	@Override
	public int compareTo(Face o) {
		return Integer.compare(getSortingIndex(), o.getSortingIndex());
	}

	public int getSortingIndex() {
		return texture == null ? -1 : texture.getSprite().getPrimitive().getId();
	}

}
