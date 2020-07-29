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
package ru.windcorp.optica.client.world.renders.bro;

import ru.windcorp.optica.client.graphics.texture.Texture;
import ru.windcorp.optica.client.world.renders.BlockRenderTexturedCube;

public class BlockRenderOpaqueCube extends BlockRenderTexturedCube {

	public BlockRenderOpaqueCube(
			String namespace, String name,
			Texture topTexture, Texture bottomTexture,
			Texture northTexture, Texture southTexture,
			Texture eastTexture, Texture westTexture
	) {
		super(
				namespace, name,
				topTexture, bottomTexture,
				northTexture, southTexture,
				eastTexture, westTexture
		);
		setOptimizer("Default:OpaqueCube");
	}

}
