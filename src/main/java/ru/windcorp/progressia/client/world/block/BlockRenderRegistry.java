/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
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
 */

package ru.windcorp.progressia.client.world.block;

import ru.windcorp.progressia.client.graphics.texture.Atlases;
import ru.windcorp.progressia.client.graphics.texture.Atlases.AtlasGroup;
import ru.windcorp.progressia.client.graphics.texture.SimpleTexture;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.common.resource.ResourceManager;
import ru.windcorp.progressia.common.util.namespaces.NamespacedInstanceRegistry;

public class BlockRenderRegistry extends NamespacedInstanceRegistry<BlockRender> {

	private static final BlockRenderRegistry INSTANCE = new BlockRenderRegistry();

	private static final AtlasGroup BLOCKS_ATLAS_GROUP = new AtlasGroup("Blocks", 1 << 12);

	public static BlockRenderRegistry getInstance() {
		return INSTANCE;
	}

	public static Texture getBlockTexture(String name) {
		return new SimpleTexture(
				Atlases.getSprite(ResourceManager.getTextureResource("blocks/" + name), BLOCKS_ATLAS_GROUP));
	}

	public static AtlasGroup getBlocksAtlasGroup() {
		return BLOCKS_ATLAS_GROUP;
	}

}
