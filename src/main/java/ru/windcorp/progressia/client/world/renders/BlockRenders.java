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
import ru.windcorp.progressia.common.resource.ResourceManager;

public class BlockRenders {
	
	private static final Map<String, BlockRender> BLOCK_RENDERS =
			new HashMap<>();
	
	private static final AtlasGroup BLOCKS_ATLAS_GROUP =
			new AtlasGroup("Blocks", 1 << 12);

	private BlockRenders() {}
	
	public static BlockRender get(String name) {
		return BLOCK_RENDERS.get(name);
	}
	
	public static void register(BlockRender blockRender) {
		BLOCK_RENDERS.put(blockRender.getId(), blockRender);
	}
	
	public static Texture getBlockTexture(String name) {
		return new SimpleTexture(
				Atlases.getSprite(
						ResourceManager.getTextureResource("blocks/" + name),
						BLOCKS_ATLAS_GROUP
				)
		);
	}

}
