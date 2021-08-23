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

package ru.windcorp.progressia.client.world.entity;

import java.io.IOException;

import ru.windcorp.progressia.client.graphics.texture.TextureLoader;
import ru.windcorp.progressia.client.graphics.texture.TexturePrimitive;
import ru.windcorp.progressia.client.graphics.texture.TextureSettings;
import ru.windcorp.progressia.common.resource.ResourceManager;
import ru.windcorp.progressia.common.util.namespaces.NamespacedInstanceRegistry;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class EntityRenderRegistry extends NamespacedInstanceRegistry<EntityRender> {

	private static final EntityRenderRegistry INSTANCE = new EntityRenderRegistry();

	public static EntityRenderRegistry getInstance() {
		return INSTANCE;
	}

	public static TexturePrimitive getEntityTexture(String name) {
		try {
			return new TexturePrimitive(TextureLoader
					.loadPixels(ResourceManager.getTextureResource("entities/" + name), new TextureSettings(false))
					.getData());
		} catch (IOException e) {
			throw CrashReports.report(e, "Could not load entity texture %s", name);
		}
	}

}
