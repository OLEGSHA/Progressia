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

package ru.windcorp.progressia.common.resource;

public class ResourceManager {

	private static final ResourceReader CLASSPATH_READER = new ClasspathResourceReader();
	private static final ResourceReader FILESYSTEM_READER = new FilesystemResourceReader();

	public static Resource getResource(String name) {
		return new Resource(name, CLASSPATH_READER);
	}

	public static Resource getFileResource(String name) {
		return new Resource(name, FILESYSTEM_READER);
	}

	public static Resource getTextureResource(String name) {
		return getResource("assets/textures/" + name + ".png");
	}

	private ResourceManager() {
	}

}
