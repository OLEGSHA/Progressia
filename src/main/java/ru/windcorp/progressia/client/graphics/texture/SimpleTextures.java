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

package ru.windcorp.progressia.client.graphics.texture;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import ru.windcorp.progressia.common.resource.Resource;
import ru.windcorp.progressia.common.resource.ResourceManager;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class SimpleTextures {

	private static final TextureSettings SETTINGS = new TextureSettings(false);

	private static final Map<Resource, Texture> TEXTURES = new HashMap<>();

	public static Texture get(Resource resource) {
		return TEXTURES.computeIfAbsent(resource, SimpleTextures::load);
	}

	public static Texture get(String textureName) {
		return get(ResourceManager.getTextureResource(textureName));
	}

	private static Texture load(Resource resource) {
		try {
			TextureDataEditor data = TextureLoader.loadPixels(resource, SETTINGS);

			return new SimpleTexture(new Sprite(new TexturePrimitive(data.getData())));
		} catch (IOException e) {
			throw CrashReports.report(e, "Could not load texture %s", resource);
		}

	}

	private SimpleTextures() {
	}

}
