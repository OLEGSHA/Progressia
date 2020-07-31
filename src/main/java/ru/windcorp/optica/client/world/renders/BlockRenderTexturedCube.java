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

import static ru.windcorp.optica.common.block.BlockFace.*;

import java.util.EnumMap;

import ru.windcorp.optica.client.graphics.model.Shapes;
import ru.windcorp.optica.client.graphics.model.WorldRenderable;
import ru.windcorp.optica.client.graphics.texture.Texture;
import ru.windcorp.optica.client.graphics.world.WorldRenderProgram;
import ru.windcorp.optica.common.block.BlockFace;

public abstract class BlockRenderTexturedCube extends BlockRender {
	
	private final EnumMap<BlockFace, Texture> textures =
			new EnumMap<>(BlockFace.class);

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
