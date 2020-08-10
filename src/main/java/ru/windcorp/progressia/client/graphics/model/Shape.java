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
package ru.windcorp.progressia.client.graphics.model;

import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

import org.lwjgl.BufferUtils;

import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.backend.VertexBufferObject;

public class Shape implements WorldRenderable {
	
	private final ShapeRenderProgram program;
	private final Face[] faces;
	private final Usage usage;
	
	private ByteBuffer vertices;
	private ShortBuffer indices;
	
	private boolean initialized = false;
	private boolean needsAssembly = true;
	private boolean needsVBOUpdate = true;
	
	private VertexBufferObject verticesVbo;
	private VertexBufferObject indicesVbo;
	
	public Shape(Usage usage, ShapeRenderProgram program, Face... faces) {
		this.program = program;
		this.faces = faces;
		this.usage = usage;
		
		configureFaces();
		program.preprocess(this);
		
		assembleBuffers();
	}

	private void configureFaces() {
		for (Face face : faces) {
			face.setShape(this);
		}
	}

	private void assembleBuffers() {
		// TODO optimize: only update faces that requested it
		
		resizeBuffers();
		
		for (Face face : faces) {
			assembleVertices(face);
			assembleIndices(face);
			face.resetUpdateFlags();
		}
		
		this.vertices.flip();
		this.indices.flip();
		
		needsAssembly = false;
		needsVBOUpdate = true;
	}

	private void resizeBuffers() {
		int verticesRequired = 0, indicesRequired = 0;
		for (Face face : faces) {
			verticesRequired += face.getVertices().remaining();
			indicesRequired  += face.getIndices().remaining();
		}
		
		if (this.vertices == null || vertices.capacity() < verticesRequired) {
			this.vertices = BufferUtils.createByteBuffer(verticesRequired);
		} else {
			this.vertices.position(0).limit(verticesRequired);
		}
		
		if (this.indices == null || this.indices.capacity() < indicesRequired) {
			this.indices = BufferUtils.createShortBuffer(indicesRequired);
		} else {
			this.indices.position(0).limit(indicesRequired);
		}
	}

	private void assembleVertices(Face face) {
		face.locationOfVertices = this.vertices.position();
		
		insertVertices(face);
		linkVerticesWith(face);
	}

	private void insertVertices(Face face) {
		ByteBuffer faceVertices = face.getVertices();
		
		faceVertices.mark();
		this.vertices.put(faceVertices);
		faceVertices.reset();
	}

	private void linkVerticesWith(Face face) {
		int limit = vertices.limit();
		int position = vertices.position();
		
		vertices.limit(position).position(face.getLocationOfVertices());
		face.vertices = vertices.slice();
		
		vertices.position(position).limit(limit);
	}

	private void assembleIndices(Face face) {
		short vertexOffset = (short) (
				face.getLocationOfVertices() / program.getBytesPerVertex()
		);
		
		face.locationOfIndices = indices.position();
		
		ShortBuffer faceIndices = face.getIndices();
		
		if (faceIndices == null) {
			for (int i = 0; i < face.getVertexCount(); ++i) {
				this.indices.put((short) (vertexOffset + i));
			}
		} else {
			for (int i = faceIndices.position(); i < faceIndices.limit(); ++i) {
				short faceIndex = faceIndices.get(i);
				faceIndex += vertexOffset;
				this.indices.put(faceIndex);
			}
		} 
	}
	
	void markForReassembly() {
		needsAssembly = true;
	}
	
	@Override
	public void render(ShapeRenderHelper helper) {
		if (!initialized) initialize();
		if (needsAssembly) assembleBuffers();
		if (needsVBOUpdate) updateVBO();
		
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
	
	public Face[] getFaces() {
		return faces;
	}
	
	public Usage getUsage() {
		return usage;
	}

}
