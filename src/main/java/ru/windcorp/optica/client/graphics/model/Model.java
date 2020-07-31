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
package ru.windcorp.optica.client.graphics.model;

import glm.mat._4.Mat4;

public abstract class Model implements WorldRenderable {
	
	private final WorldRenderable[] parts;
	
	public Model(WorldRenderable[] parts) {
		this.parts = parts;
	}

	protected abstract Mat4 getTransform(int partIndex);
	
	@Override
	public void render(ShapeRenderHelper helper) {
		for (int i = 0; i < parts.length; ++i) {
			WorldRenderable part = parts[i];
			Mat4 transform = getTransform(i);
			
			try {
				helper.pushWorldTransform().mul(transform);
				part.render(helper);
			} finally {	
				helper.popWorldTransform();
			}
		}
	}

}
