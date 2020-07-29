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
import ru.windcorp.optica.client.graphics.world.WorldRenderer;

public class EmptyModel extends Model {
	
	private static final EmptyModel INSTANCE = new EmptyModel();
	
	private EmptyModel() {
		super(new WorldRenderable[0]);
	}
	
	public static EmptyModel getInstance() {
		return INSTANCE;
	}
	
	@Override
	public void render(WorldRenderer renderer) {
		// Do nothing
	}
	
	@Override
	protected Mat4 getTransform(int shapeIndex) {
		throw new UnsupportedOperationException();
	}

}
