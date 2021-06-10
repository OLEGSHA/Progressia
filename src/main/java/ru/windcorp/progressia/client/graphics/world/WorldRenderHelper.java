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

package ru.windcorp.progressia.client.graphics.world;

import glm.mat._4.Mat4;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.common.util.StashingStack;

public class WorldRenderHelper extends ShapeRenderHelper {

	private final StashingStack<Mat4> viewTransformStack = new StashingStack<>(TRANSFORM_STACK_SIZE, Mat4::new);

	{
		viewTransformStack.push().identity();
	}

	private final Mat4 finalTransform = new Mat4();

	public Mat4 pushViewTransform() {
		Mat4 previous = viewTransformStack.getHead();
		return viewTransformStack.push().set(previous);
	}

	public void popViewTransform() {
		viewTransformStack.removeHead();
	}

	public Mat4 getViewTransform() {
		return viewTransformStack.getHead();
	}

	@Override
	public Mat4 getFinalTransform() {
		return finalTransform.set(getViewTransform()).mul(getTransform());
	}

	@Override
	public void reset() {
		super.reset();
		viewTransformStack.removeAll();
		viewTransformStack.push().identity();
	}

}
