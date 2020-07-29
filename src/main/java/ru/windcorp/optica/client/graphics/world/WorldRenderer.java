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
package ru.windcorp.optica.client.graphics.world;

import glm.mat._4.Mat4;
import ru.windcorp.optica.common.util.StashingStack;

public class WorldRenderer {
	
	private static final int TRANSFORM_STACK_SIZE = 64;
	
	private final StashingStack<Mat4> worldTransformStack = new StashingStack<>(
			TRANSFORM_STACK_SIZE, Mat4::new
	);
	
	private final StashingStack<Mat4> viewTransformStack = new StashingStack<>(
			TRANSFORM_STACK_SIZE, Mat4::new
	);
	
	private final Mat4 finalTransform = new Mat4();
	
	{
		reset();
	}
	
	public Mat4 pushWorldTransform() {
		Mat4 previous = worldTransformStack.getHead();
		return worldTransformStack.push().set(previous);
	}
	
	public void popWorldTransform() {
		worldTransformStack.removeHead();
	}
	
	public Mat4 getWorldTransform() {
		return worldTransformStack.getHead();
	}
	
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
	
	public Mat4 getFinalTransform() {
		return finalTransform.set(getViewTransform()).mul(getWorldTransform());
	}
	
	public void reset() {
		worldTransformStack.removeAll();
		worldTransformStack.push().identity();
		viewTransformStack.removeAll();
		viewTransformStack.push().identity();
	}

}
