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

public class TileRenders {
	
	private static final Map<String, TileRender> TILE_RENDERS =
			new HashMap<>();
	
	private static final AtlasGroup TILES_ATLAS_GROUP =
			new AtlasGroup("Tiles", 1 << 12);

	private TileRenders() {}
	
	public static void registerTest() {
		register(new TileRenderSimple("Test", "Stones", getTexture("stones")));
	}
	
	public static TileRender get(String name) {
		return TILE_RENDERS.get(name);
	}
	
	public static void register(TileRender tileRender) {
		TILE_RENDERS.put(tileRender.getId(), tileRender);
	}
	
	public static Texture getTexture(String name) {
		return new SimpleTexture(
				Atlases.getSprite(
						ResourceManager.getTextureResource("tiles/" + name),
						TILES_ATLAS_GROUP
				)
		);
	}

}
