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
package ru.windcorp.optica.client.graphics.flat;

import java.nio.FloatBuffer;

import glm.mat._4.Mat4;
import ru.windcorp.optica.client.graphics.backend.GraphicsInterface;
import ru.windcorp.optica.client.graphics.model.ShapeRenderHelper;

public abstract class FlatRenderHelper extends ShapeRenderHelper {
	
	protected static final float MAX_DEPTH = 1 << 16;
	
	protected final Mat4 finalTransform = new Mat4();
	
	protected abstract FloatBuffer getMasks();
	
	@Override
	public Mat4 getFinalTransform() {
		float width = GraphicsInterface.getFramebufferWidth();
		float height = GraphicsInterface.getFramebufferHeight();
		
		return finalTransform.identity().translate(-1, +1, 0)
	              .scale(2 / width, -2 / height, 1 / MAX_DEPTH)
	              .mul(getTransform());
	}

}
