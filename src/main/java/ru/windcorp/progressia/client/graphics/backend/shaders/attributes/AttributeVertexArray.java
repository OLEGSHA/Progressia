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

package ru.windcorp.progressia.client.graphics.backend.shaders.attributes;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import ru.windcorp.progressia.client.graphics.backend.VertexBufferObject;
import ru.windcorp.progressia.client.graphics.backend.shaders.Program;

public class AttributeVertexArray extends Attribute {

	private boolean isEnabled = false;

	public AttributeVertexArray(int handle, Program program) {
		super(handle, program);
	}

	public void enable() {
		if (!isEnabled) {
			glEnableVertexAttribArray(handle);
			isEnabled = true;
		}
	}

	public void disable() {
		if (isEnabled) {
			glDisableVertexAttribArray(handle);
			isEnabled = false;
		}
	}

	public void set(int size, boolean normalized, int stride, ByteBuffer pointer) {
		glVertexAttribPointer(handle, size, GL_BYTE, normalized, stride, pointer);
	}

	public void set(int size, boolean normalized, int stride, FloatBuffer pointer) {
		glVertexAttribPointer(handle, size, GL_FLOAT, normalized, stride, pointer);
	}

	public void set(int size, boolean normalized, int stride, IntBuffer pointer) {
		glVertexAttribPointer(handle, size, GL_INT, normalized, stride, pointer);
	}

	public void set(int size, boolean normalized, int stride, ShortBuffer pointer) {
		glVertexAttribPointer(handle, size, GL_SHORT, normalized, stride, pointer);
	}

	public void set(int size, int type, boolean normalized, int stride, long pointer) {
		glVertexAttribPointer(handle, size, type, normalized, stride, pointer);
	}

	public void set(int size, int type, boolean normalized, int stride, VertexBufferObject vbo, long offset) {
		glBindBuffer(GL_ARRAY_BUFFER, vbo.getHandle());
		glVertexAttribPointer(handle, size, type, normalized, stride, offset);
	}

}
