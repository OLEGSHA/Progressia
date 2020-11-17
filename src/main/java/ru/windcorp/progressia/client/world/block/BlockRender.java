/*******************************************************************************
 * Progressia
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
package ru.windcorp.progressia.client.world.block;

import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.client.graphics.model.Renderable;

public abstract class BlockRender extends Namespaced {
	
	public BlockRender(String id) {
		super(id);
	}

	public void render(ShapeRenderHelper renderer) {
		throw new UnsupportedOperationException(
				"BlockRender.render() not implemented in " + this
		);
	}
	
	public Renderable createRenderable() {
		return null;
	}
	
	public boolean needsOwnRenderable() {
		return true;
	}

}
