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
package ru.windcorp.progressia.client.world.renders;

import static ru.windcorp.progressia.common.block.BlockFace.*;

import java.util.HashMap;
import java.util.Map;

import ru.windcorp.progressia.client.graphics.model.Shapes;
import ru.windcorp.progressia.client.graphics.model.WorldRenderable;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.renders.bro.BlockRenderCubeOptimizer.OpaqueCube;
import ru.windcorp.progressia.common.block.BlockFace;

public abstract class BlockRenderTexturedCube
extends BlockRender
implements OpaqueCube {
	
	private final Map<BlockFace, Texture> textures = new HashMap<>();

	public BlockRenderTexturedCube(
			String namespace, String name,
			Texture topTexture, Texture bottomTexture,
			Texture northTexture, Texture southTexture,
			Texture eastTexture, Texture westTexture
	) {
		super(namespace, name);
		
		textures.put(TOP,    topTexture);
		textures.put(BOTTOM, bottomTexture);
		textures.put(NORTH,  northTexture);
		textures.put(SOUTH,  southTexture);
		textures.put(EAST,   eastTexture);
		textures.put(WEST,   westTexture);
	}
	
	@Override
	public Texture getTexture(BlockFace face) {
		return textures.get(face);
	}
	
	@Override
	public WorldRenderable createRenderable() {
		return new Shapes.PppBuilder(
				WorldRenderProgram.getDefault(),
				getTexture(TOP), getTexture(BOTTOM),
				getTexture(NORTH), getTexture(SOUTH),
				getTexture(EAST), getTexture(WEST)
		).create();
	}

}
