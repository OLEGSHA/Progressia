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
			TextureDataEditor data =
					TextureLoader.loadPixels(resource, SETTINGS);
			
			return new SimpleTexture(
					new Sprite(new TexturePrimitive(data.getData()))
			);
		} catch (IOException e) {
			CrashReports.report(e, "Problem with load texture");
			return null;
		}

	}

	private SimpleTextures() {}

}
