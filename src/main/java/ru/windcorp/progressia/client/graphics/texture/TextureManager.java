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
package ru.windcorp.progressia.client.graphics.texture;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import ru.windcorp.progressia.client.graphics.backend.RenderTaskQueue;
import ru.windcorp.progressia.common.resource.Resource;
import ru.windcorp.progressia.common.resource.ResourceManager;
import ru.windcorp.progressia.common.util.ByteBufferInputStream;

public class TextureManager {
	
	private static final String TEXTURE_ASSETS_PREFIX = "assets/textures/";
	
	private static final Map<String, Pixels> LOADED_PIXELS =
			new HashMap<>();
	
	private static final Map<String, TexturePrimitive> LOADED_PRIMITIVES =
			new HashMap<>();
	
	private static Pixels getCachedPixels(String name) {
		return LOADED_PIXELS.get(name);
	}
	
	private static Pixels getCachedPixels(Resource resource) {
		return getCachedPixels(resource.getName());
	}
	
	private static Pixels cachePixels(Pixels pixels, String name) {
		LOADED_PIXELS.put(name, pixels);
		return pixels;
	}
	
	private static Pixels cachePixels(Pixels pixels, Resource resource) {
		return cachePixels(pixels, resource.getName());
	}
	
	private static TexturePrimitive getCachedPrimitive(String name) {
		return LOADED_PRIMITIVES.get(name);
	}
	
	private static TexturePrimitive getCachedPrimitive(Resource resource) {
		return getCachedPrimitive(resource.getName());
	}
	
	private static TexturePrimitive cachePrimitive(
			TexturePrimitive primitive,
			String name
	) {
		LOADED_PRIMITIVES.put(name, primitive);
		return primitive;
	}
	
	private static TexturePrimitive cachePrimitive(
			TexturePrimitive primitive,
			Resource resource
	) {
		return cachePrimitive(primitive, resource.getName());
	}
	
	private static Resource getResource(String textureName) {
		return ResourceManager.getResource(
				TEXTURE_ASSETS_PREFIX + textureName + ".png"
		);
	}
	
	public static Pixels createOpenGLBuffer(
			InputStream stream,
			TextureSettings settings
	) {
		try {
			return PngLoader.loadPngImage(stream, settings);
		} catch (IOException e) {
			throw new RuntimeException("u stupid. refresh ur project");
		}
	}
	
	public static Pixels createOpenGLBuffer(
			Resource resource,
			TextureSettings settings
	) {
		Pixels cache = getCachedPixels(resource);
		if (cache != null) return cache;
		return cachePixels(
				createOpenGLBuffer(resource.getInputStream(), settings),
				resource
		);
	}
	
	public static Pixels createOpenGLBuffer(
			String textureName,
			TextureSettings settings
	) {
		Pixels cache = getCachedPixels(textureName);
		if (cache != null) return cache;
		return cachePixels(
				createOpenGLBuffer(getResource(textureName), settings),
				textureName
		);
	}
	
	public static Pixels createOpenGLBuffer(
			ByteBuffer bytes,
			TextureSettings settings
	) {
		bytes.mark();
		try {
			return createOpenGLBuffer(
					new ByteBufferInputStream(bytes), settings
			);
		} finally {
			bytes.reset();
		}
	}
	
	public static TexturePrimitive createTexturePrimitive(
			InputStream stream,
			TextureSettings settings
	) {
		Pixels pixels = createOpenGLBuffer(stream, settings);
		TexturePrimitive result = new TexturePrimitive(pixels);
		return result;
	}
	
	public static TexturePrimitive createTexturePrimitive(
			Resource resource,
			TextureSettings settings
	) {
		TexturePrimitive primitive = getCachedPrimitive(resource);
		if (primitive != null) return primitive;
		return cachePrimitive(
				createTexturePrimitive(resource.getInputStream(), settings),
				resource
		);
	}
	
	public static TexturePrimitive createTexturePrimitive(
			String textureName,
			TextureSettings settings
	) {
		TexturePrimitive primitive = getCachedPrimitive(textureName);
		if (primitive != null) return primitive;
		return cachePrimitive(
				createTexturePrimitive(getResource(textureName), settings),
				textureName
		);
	}
	
	public static TexturePrimitive createTexturePrimitive(
			ByteBuffer bytes,
			TextureSettings settings
	) {
		bytes.mark();
		try {
			return createTexturePrimitive(
					new ByteBufferInputStream(bytes), settings
			);
		} finally {
			bytes.reset();
		}
	}
	
	public static TexturePrimitive load(
			InputStream stream,
			TextureSettings settings
	) {
		TexturePrimitive result = createTexturePrimitive(stream, settings);
		if (!result.isLoaded()) RenderTaskQueue.invokeLater(result::load);
		return result;
	}

	public static TexturePrimitive load(
			Resource resource,
			TextureSettings settings
	) {
		return load(resource.getInputStream(), settings);
	}

	public static TexturePrimitive load(
			String textureName,
			TextureSettings settings
	) {
		return load(getResource(textureName), settings);
	}
	
	public static TexturePrimitive load(
			ByteBuffer bytes,
			TextureSettings settings
	) {
		bytes.mark();
		try {
			return load(new ByteBufferInputStream(bytes), settings);
		} finally {
			bytes.reset();
		}
	}

}