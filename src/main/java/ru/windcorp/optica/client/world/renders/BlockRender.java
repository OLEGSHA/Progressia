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
package ru.windcorp.optica.client.world.renders;

import ru.windcorp.optica.client.graphics.model.WorldRenderable;
import ru.windcorp.optica.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.optica.common.util.Namespaced;

public abstract class BlockRender extends Namespaced {
	
	private String optimizer = null;
	
	public BlockRender(String namespace, String name) {
		super(namespace, name);
	}
	
	public String getOptimizer() {
		return optimizer;
	}
	
	public boolean isOptimized() {
		return getOptimizer() != null;
	}
	
	public void setOptimizer(String optimizer) {
		this.optimizer = optimizer;
	}

	public void render(ShapeRenderHelper renderer) {
		throw new UnsupportedOperationException(
				"BlockRender.render() not implemented"
		);
	}
	
	public WorldRenderable createRenderable() {
		return null;
	}

}
