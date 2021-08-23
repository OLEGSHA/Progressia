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

import glm.mat._4.Mat4;

public class DefaultFlatRenderHelper extends FlatRenderHelper {

	private final TransformedMask transformer = new TransformedMask();

	protected final MaskStack maskStack = new MaskStack();

	protected final boolean[] hasMask = new boolean[TRANSFORM_STACK_SIZE];

	public void pushMask(Mask mask, Mat4 transform) {
		pushMask(transformer.set(mask, transform), transform);
	}

	public void pushMask(TransformedMask mask, Mat4 transform) {
		hasMask[transformStack.getSize()] = true;
		pushTransform().mul(transform);
		maskStack.pushMask(mask);
	}

	@Override
	public Mat4 pushTransform() {
		hasMask[transformStack.getSize()] = false;
		return super.pushTransform();
	}

	@Override
	public void popTransform() {
		super.popTransform();

		if (hasMask[transformStack.getSize()]) {
			maskStack.popMask();
		}
	}

	@Override
	public void reset() {
		super.reset();
		maskStack.clear();
	}

	@Override
	protected FloatBuffer getMasks() {
		return maskStack.getBuffer();
	}

}
