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
package ru.windcorp.progressia.client.world.tile;

import ru.windcorp.progressia.client.graphics.texture.Atlases;
import ru.windcorp.progressia.client.graphics.texture.Atlases.AtlasGroup;
import ru.windcorp.progressia.client.graphics.texture.SimpleTexture;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.common.resource.ResourceManager;
import ru.windcorp.progressia.common.util.NamespacedRegistry;

public class TileRenderRegistry extends NamespacedRegistry<TileRender> {
	
	private static final TileRenderRegistry INSTANCE = new TileRenderRegistry();
	
	private static final AtlasGroup TILES_ATLAS_GROUP =
			new AtlasGroup("Tiles", 1 << 12);

	public static TileRenderRegistry getInstance() {
		return INSTANCE;
	}
	
	public static AtlasGroup getTilesAtlasGroup() {
		return TILES_ATLAS_GROUP;
	}
	
	public static Texture getTileTexture(String name) {
		return new SimpleTexture(
				Atlases.getSprite(
						ResourceManager.getTextureResource("tiles/" + name),
						TILES_ATLAS_GROUP
				)
		);
	}

}