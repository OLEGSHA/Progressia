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

import glm.mat._4.Mat4;
import glm.vec._4.Vec4;
import ru.windcorp.progressia.client.graphics.Colors;
import ru.windcorp.progressia.common.util.StashingStack;

public class ShapeRenderHelper {

	protected static final int TRANSFORM_STACK_SIZE = 64;
	protected static final int COLOR_MULTIPLIER_STACK_SIZE = TRANSFORM_STACK_SIZE;

	protected final StashingStack<Mat4> transformStack = new StashingStack<>(TRANSFORM_STACK_SIZE, Mat4::new);

	protected final StashingStack<Vec4> colorMultiplierStack = new StashingStack<>(COLOR_MULTIPLIER_STACK_SIZE,
			Vec4::new);

	{
		transformStack.push().identity();
		colorMultiplierStack.push().set(Colors.WHITE);
	}

	public Mat4 pushTransform() {
		Mat4 previous = transformStack.getHead();
		return transformStack.push().set(previous);
	}

	public void popTransform() {
		transformStack.removeHead();
	}

	public Mat4 getTransform() {
		return transformStack.getHead();
	}

	public Mat4 getFinalTransform() {
		return getTransform();
	}

	public Vec4 pushColorMultiplier() {
		Vec4 previous = colorMultiplierStack.getHead();
		return colorMultiplierStack.push().set(previous);
	}

	public void popColorMultiplier() {
		colorMultiplierStack.removeHead();
	}

	public Vec4 getColorMultiplier() {
		return colorMultiplierStack.getHead();
	}

	public Vec4 getFinalColorMultiplier() {
		return getColorMultiplier();
	}

	public void reset() {
		transformStack.removeAll();
		transformStack.push().identity();
		colorMultiplierStack.removeAll();
		colorMultiplierStack.push().set(Colors.WHITE);
	}

}
