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
package ru.windcorp.progressia.client.world;

import ru.windcorp.progressia.client.graphics.backend.GraphicsInterface;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;

public abstract class UpdatingRenderable implements Renderable {
	
	private long stateComputedForFrame = -1;

	/**
	 * Updates the state of this model. This method is invoked exactly once per
	 * renderable per frame before this model is queried for the first time.
	 */
	protected void update() {
		// Do nothing
	}
	
	protected void updateIfNecessary() {
		if (stateComputedForFrame != GraphicsInterface.getFramesRendered()) {
			update();
			stateComputedForFrame = GraphicsInterface.getFramesRendered();
		}
	}
	
	@Override
	public final void render(ShapeRenderHelper renderer) {
		updateIfNecessary();
		doRender(renderer);
	}
	
	protected abstract void doRender(ShapeRenderHelper renderer);

}
