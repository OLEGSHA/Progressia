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

import java.util.HashMap;
import java.util.Map;

import ru.windcorp.optica.client.graphics.texture.SimpleTexture;
import ru.windcorp.optica.client.graphics.texture.Sprite;
import ru.windcorp.optica.client.graphics.texture.Texture;
import ru.windcorp.optica.client.graphics.texture.TextureManager;
import ru.windcorp.optica.client.world.renders.bro.BlockRenderOpaqueCube;

public class BlockRenders {
	
	private static Texture grassTop = qtex("grass_top");
	private static Texture grassSide = qtex("grass_side");
	private static Texture dirtT = qtex("grass_bottom");
	private static Texture stoneT = qtex("stone");
	private static Texture glassT = qtex("glass_clear");
	
	private static final Map<String, BlockRender> BLOCK_RENDERS =
			new HashMap<>();

	private BlockRenders() {}
	
	static {
		register(new BlockRenderOpaqueCube("Test", "Grass", grassTop, dirtT, grassSide, grassSide, grassSide, grassSide));
		register(new BlockRenderOpaqueCube("Test", "Dirt", dirtT, dirtT, dirtT, dirtT, dirtT, dirtT));
		register(new BlockRenderOpaqueCube("Test", "Stone", stoneT, stoneT, stoneT, stoneT, stoneT, stoneT));

		register(new BlockRenderOpaqueCube("Test", "Compass", qtex("compass"), qtex("compass"), qtex("side_north"), qtex("side_south"), qtex("side_east"), qtex("side_west")));
		
		register(new BlockRenderNone("Test", "Air"));
		register(new BlockRenderTransparentCube("Test", "Glass", glassT, glassT, glassT, glassT, glassT, glassT));
	}
	
	public static BlockRender get(String name) {
		return BLOCK_RENDERS.get(name);
	}
	
	public static void register(BlockRender blockRender) {
		BLOCK_RENDERS.put(blockRender.getId(), blockRender);
	}
	
	private static Texture qtex(String name) {
		return new SimpleTexture(new Sprite(TextureManager.load(name, false)));
	}

}
