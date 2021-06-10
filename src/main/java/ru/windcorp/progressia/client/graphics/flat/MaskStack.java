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

package ru.windcorp.progressia.client.graphics.flat;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;

public class MaskStack {

	private final FloatBuffer buffer = BufferUtils
			.createFloatBuffer(FlatRenderProgram.MASK_STACK_SIZE * TransformedMask.SIZE_IN_FLOATS);

	public void pushMask(TransformedMask mask) {
		mask.writeToBuffer(buffer);
	}

	public void popMask() {
		buffer.position(buffer.position() - TransformedMask.SIZE_IN_FLOATS);
	}

	public void clear() {
		buffer.clear();
	}

	FloatBuffer getBuffer() {
		return buffer;
	}

}
