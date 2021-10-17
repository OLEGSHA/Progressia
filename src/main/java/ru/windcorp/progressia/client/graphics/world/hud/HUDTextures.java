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
package ru.windcorp.progressia.client.graphics.world.hud;

import java.io.IOException;

import ru.windcorp.progressia.client.graphics.texture.Atlases;
import ru.windcorp.progressia.client.graphics.texture.SimpleTexture;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.texture.Atlases.AtlasGroup;
import ru.windcorp.progressia.common.resource.ResourceManager;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class HUDTextures {

	private static final AtlasGroup HUD_ATLAS_GROUP = new AtlasGroup("HUD", 1 << 12);
	
	private static ItemAmountTypeface itemAmountTypeface = null;

	public static Texture getHUDTexture(String name) {
		return new SimpleTexture(
			Atlases.getSprite(
				ResourceManager.getTextureResource("gui/" + name),
				HUD_ATLAS_GROUP
			)
		);
	}

	public static AtlasGroup getHUDAtlasGroup() {
		return HUD_ATLAS_GROUP;
	}
	
	public static ItemAmountTypeface getItemAmountTypeface() {
		return itemAmountTypeface;
	}
	
	public static void loadItemAmountTypeface() {
		try {
			itemAmountTypeface = new ItemAmountTypeface();
		} catch (IOException e) {
			throw CrashReports.report(e, "Could not load item amount typeface");
		}
	}

}
