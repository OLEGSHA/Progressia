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

package ru.windcorp.progressia.client.graphics.backend;

import static org.lwjgl.opengl.GL20.*;

import java.nio.*;

import org.lwjgl.opengl.GL20;
import ru.windcorp.progressia.client.graphics.backend.OpenGLObjectTracker.OpenGLDeletable;

public class VertexBufferObject implements OpenGLDeletable {

	public static enum BindTarget {
		ARRAY(GL_ARRAY_BUFFER), ELEMENT_ARRAY(GL_ELEMENT_ARRAY_BUFFER);

		private final int glCode;

		private BindTarget(int glCode) {
			this.glCode = glCode;
		}

		public int getGlCode() {
			return glCode;
		}
	}

	private final int handle;

	private long length = 0;
	private final Usage usage;

	public VertexBufferObject(Usage usage) {
		handle = glGenBuffers();
		OpenGLObjectTracker.register(this, GL20::glDeleteBuffers);

		this.usage = usage;
	}

	public void setData(ByteBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, data, usage.getGlCode());
		length = data.remaining();
	}

	public void setData(double[] data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, data, usage.getGlCode());
		length = data.length * Double.BYTES;
	}

	public void setData(DoubleBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, data, usage.getGlCode());
		length = data.remaining() * Double.BYTES;
	}

	public void setData(float[] data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, data, usage.getGlCode());
		length = data.length * Float.BYTES;
	}

	public void setData(FloatBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, data, usage.getGlCode());
		length = data.remaining() * Float.BYTES;
	}

	public void setData(int[] data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, data, usage.getGlCode());
		length = data.length * Integer.BYTES;
	}

	public void setData(IntBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, data, usage.getGlCode());
		length = data.remaining() * Integer.BYTES;
	}

	public void setData(long[] data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, data, usage.getGlCode());
		length = data.length * Long.BYTES;
	}

	public void setData(LongBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, data, usage.getGlCode());
		length = data.remaining() * Long.BYTES;
	}

	public void setData(short[] data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, data, usage.getGlCode());
		length = data.length * Short.BYTES;
	}

	public void setData(ShortBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, data, usage.getGlCode());
		length = data.remaining() * Short.BYTES;
	}

	public void initData(long length) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferData(GL_ARRAY_BUFFER, length, usage.getGlCode());
		this.length = length;
	}

	public void setData(int offset, ByteBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferSubData(GL_ARRAY_BUFFER, offset, data);
	}

	public void setData(int offset, double[] data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferSubData(GL_ARRAY_BUFFER, offset, data);
	}

	public void setData(int offset, DoubleBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferSubData(GL_ARRAY_BUFFER, offset, data);
	}

	public void setData(int offset, float[] data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferSubData(GL_ARRAY_BUFFER, offset, data);
	}

	public void setData(int offset, FloatBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferSubData(GL_ARRAY_BUFFER, offset, data);
	}

	public void setData(int offset, int[] data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferSubData(GL_ARRAY_BUFFER, offset, data);
	}

	public void setData(int offset, IntBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferSubData(GL_ARRAY_BUFFER, offset, data);
	}

	public void setData(int offset, long[] data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferSubData(GL_ARRAY_BUFFER, offset, data);
	}

	public void setData(int offset, LongBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferSubData(GL_ARRAY_BUFFER, offset, data);
	}

	public void setData(int offset, short[] data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferSubData(GL_ARRAY_BUFFER, offset, data);
	}

	public void setData(int offset, ShortBuffer data) {
		glBindBuffer(GL_ARRAY_BUFFER, handle);
		glBufferSubData(GL_ARRAY_BUFFER, offset, data);
	}

	public void bind(BindTarget target) {
		glBindBuffer(target.getGlCode(), handle);
	}

	public long getLength() {
		return length;
	}

	public Usage getUsage() {
		return usage;
	}

	@Override
	public int getHandle() {
		return handle;
	}
}
