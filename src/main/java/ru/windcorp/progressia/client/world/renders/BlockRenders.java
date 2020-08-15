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

import java.util.HashMap;
import java.util.Map;

import ru.windcorp.progressia.client.graphics.texture.Atlases;
import ru.windcorp.progressia.client.graphics.texture.Atlases.AtlasGroup;
import ru.windcorp.progressia.client.graphics.texture.SimpleTexture;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.world.renders.bro.BlockRenderOpaqueCube;
import ru.windcorp.progressia.common.resource.ResourceManager;

public class BlockRenders {
	
	private static final Map<String, BlockRender> BLOCK_RENDERS =
			new HashMap<>();
	
	private static final AtlasGroup BLOCKS_ATLAS_GROUP =
			new AtlasGroup("Blocks", 1 << 6);
	
	private static Texture grassTop = getTexture("grass_top");
	private static Texture grassSide = getTexture("grass_side");
	private static Texture dirt = getTexture("grass_bottom");
	private static Texture stone = getTexture("stone");
	private static Texture glass = getTexture("glass_clear");
	private static Texture compass = getTexture("compass");

	private BlockRenders() {}
	
	public static void registerTest() {
		register(new BlockRenderOpaqueCube("Test", "Grass", grassTop, dirt, grassSide, grassSide, grassSide, grassSide));
		register(new BlockRenderOpaqueCube("Test", "Dirt", dirt, dirt, dirt, dirt, dirt, dirt));
		register(new BlockRenderOpaqueCube("Test", "Stone", stone, stone, stone, stone, stone, stone));

		register(new BlockRenderOpaqueCube("Test", "Compass", compass, compass, getTexture("side_north"), getTexture("side_south"), getTexture("side_east"), getTexture("side_west")));
		
		register(new BlockRenderNone("Test", "Air"));
		register(new BlockRenderTransparentCube("Test", "Glass", glass, glass, glass, glass, glass, glass));
	}
	
	public static BlockRender get(String name) {
		return BLOCK_RENDERS.get(name);
	}
	
	public static void register(BlockRender blockRender) {
		BLOCK_RENDERS.put(blockRender.getId(), blockRender);
	}
	
	public static Texture getTexture(String name) {
		return new SimpleTexture(
				Atlases.getSprite(
						ResourceManager.getTextureResource("blocks/" + name),
						BLOCKS_ATLAS_GROUP
				)
		);
	}

}
