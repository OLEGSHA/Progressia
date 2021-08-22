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
import java.util.Arrays;

import org.lwjgl.BufferUtils;

import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.backend.VertexBufferObject;

public class Shape implements Renderable {

	private final ShapeRenderProgram program;
	private final ShapePart[] parts;
	private final Usage usage;

	private ShapePartGroup[] groups;

	private ByteBuffer vertices;
	private ShortBuffer indices;

	private boolean initialized = false;
	private boolean needsAssembly = true;
	private boolean needsVBOUpdate = true;

	private VertexBufferObject verticesVbo;
	private VertexBufferObject indicesVbo;

	public Shape(Usage usage, ShapeRenderProgram program, ShapePart... parts) {
		this.program = program;
		this.parts = parts;
		this.usage = usage;

		configureParts();
		program.preprocess(this);

		assembleBuffers();
	}

	private void configureParts() {
		for (ShapePart part : parts) {
			part.setShape(this);
		}
	}

	private void assembleBuffers() {
		// TODO optimize: only update faces that requested it

		sortParts();
		resizeBuffers();

		for (ShapePart part : parts) {
			assembleVertices(part);
			assembleIndices(part);
			part.resetUpdateFlags();
		}

		this.vertices.flip();
		this.indices.flip();

		assembleGroups();

		needsAssembly = false;
		needsVBOUpdate = true;
	}

	private void resizeBuffers() {
		int verticesRequired = 0, indicesRequired = 0;
		for (ShapePart part : parts) {
			verticesRequired += part.getVertices().remaining();
			indicesRequired += part.getIndices().remaining();
		}

		if (vertices == null || vertices.capacity() < verticesRequired) {
			this.vertices = BufferUtils.createByteBuffer(verticesRequired);
		} else {
			vertices.position(0).limit(verticesRequired);
		}

		if (indices == null || indices.capacity() < indicesRequired) {
			this.indices = BufferUtils.createShortBuffer(indicesRequired);
		} else {
			indices.position(0).limit(indicesRequired);
		}
	}

	private void assembleVertices(ShapePart part) {
		part.locationOfVertices = this.vertices.position();

		insertVertices(part);
		linkVerticesWith(part);
	}

	private void insertVertices(ShapePart part) {
		ByteBuffer partVertices = part.getVertices();

		partVertices.mark();
		this.vertices.put(partVertices);
		partVertices.reset();
	}

	private void linkVerticesWith(ShapePart part) {
		int limit = vertices.limit();
		int position = vertices.position();

		vertices.limit(position).position(part.getLocationOfVertices());
		part.vertices = vertices.slice();

		vertices.position(position).limit(limit);
	}

	private void assembleIndices(ShapePart part) {
		short vertexOffset = (short) (part.getLocationOfVertices() / program.getBytesPerVertex());

		part.locationOfIndices = indices.position();

		ShortBuffer partIndices = part.getIndices();

		if (partIndices == null) {
			for (int i = 0; i < part.getVertexCount(); ++i) {
				this.indices.put((short) (vertexOffset + i));
			}
		} else {
			for (int i = partIndices.position(); i < partIndices.limit(); ++i) {
				short partIndex = partIndices.get(i);
				partIndex += vertexOffset;
				this.indices.put(partIndex);
			}
		}
	}

	private void sortParts() {
		Arrays.sort(parts);
	}

	private void assembleGroups() {
		int unique = countUniqueParts();
		this.groups = new ShapePartGroup[unique];

		if (parts.length == 0)
			return;

		int previousHandle = parts[0].getSortingIndex();
		int start = 0;
		int groupIndex = 0;

		for (int i = 1; i < parts.length; ++i) {
			if (previousHandle != parts[i].getSortingIndex()) {

				groups[groupIndex] = new ShapePartGroup(parts, start, i);
				start = i;
				groupIndex++;

				previousHandle = parts[i].getSortingIndex();
			}
		}

		assert groupIndex == groups.length - 1;
		groups[groupIndex] = new ShapePartGroup(parts, start, parts.length);
	}

	private int countUniqueParts() {
		if (parts.length == 0)
			return 0;

		int result = 1;
		int previousHandle = parts[0].getSortingIndex();

		for (int i = 1; i < parts.length; ++i) {
			if (previousHandle != parts[i].getSortingIndex()) {
				result++;
				previousHandle = parts[i].getSortingIndex();
			}
		}

		return result;
	}

	void markForReassembly() {
		needsAssembly = true;
	}

	@Override
	public void render(ShapeRenderHelper helper) {
		if (!initialized)
			initialize();
		if (needsAssembly)
			assembleBuffers();
		if (needsVBOUpdate)
			updateVBO();

		program.render(helper, this);
	}

	private void initialize() {
		verticesVbo = new VertexBufferObject(usage);
		indicesVbo = new VertexBufferObject(usage);
		needsVBOUpdate = true;

		initialized = true;
	}

	private void updateVBO() {
		verticesVbo.setData(vertices);
		indicesVbo.setData(indices);

		needsVBOUpdate = false;
	}

	VertexBufferObject getVerticesVbo() {
		return verticesVbo;
	}

	VertexBufferObject getIndicesVbo() {
		return indicesVbo;
	}

	public ShapeRenderProgram getProgram() {
		return program;
	}

	public ShapePart[] getParts() {
		return parts;
	}

	public ShapePartGroup[] getGroups() {
		return groups;
	}

	public Usage getUsage() {
		return usage;
	}

}
