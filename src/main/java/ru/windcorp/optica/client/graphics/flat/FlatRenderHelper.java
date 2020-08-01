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

import ru.windcorp.optica.client.graphics.backend.GraphicsInterface;
import ru.windcorp.optica.client.graphics.model.ShapeRenderHelper;

public class FlatRenderHelper extends ShapeRenderHelper {
	
	private static final float MAX_DEPTH = 1 << 16;
	
	private final Mask mask = new Mask();
	
	{
		setupScreenTransform();
	}
	
	public FlatRenderHelper pushMask(
			int startX, int startY,
			int endX, int endY
	) {
		mask.set(startX, startY, endX, endY);
		pushTransform().translate(startX, startY, 0);
		return this;
	}
	
	public FlatRenderHelper pushMask(Mask mask) {
		return pushMask(
				mask.getStartX(), mask.getStartY(),
				mask.getEndX(), mask.getEndY()
		);
	}

	public int getStartX() {
		return mask.getStartX();
	}

	public int getStartY() {
		return mask.getStartY();
	}

	public int getEndX() {
		return mask.getEndX();
	}

	public int getEndY() {
		return mask.getEndY();
	}
	
	public boolean isRenderable() {
		return !mask.isEmpty();
	}

	@Override
	public void reset() {
		super.reset();
		
		setupScreenTransform();
		mask.set(0, 0, Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	private void setupScreenTransform() {
		float width = GraphicsInterface.getFramebufferWidth();
		float height = GraphicsInterface.getFramebufferHeight();

		getTransform().translate(-1, +1, 0)
		              .scale(2 / width, -2 / height, 1 / MAX_DEPTH);
	}

}
