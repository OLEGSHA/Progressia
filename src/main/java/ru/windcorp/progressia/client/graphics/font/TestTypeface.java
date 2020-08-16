package ru.windcorp.progressia.client.graphics.font;

import java.io.IOException;

import glm.vec._2.Vec2;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.map.hash.TCharObjectHashMap;
import ru.windcorp.progressia.client.graphics.flat.FlatRenderProgram;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram;
import ru.windcorp.progressia.client.graphics.texture.SimpleTexture;
import ru.windcorp.progressia.client.graphics.texture.Sprite;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.texture.TextureLoader;
import ru.windcorp.progressia.client.graphics.texture.TexturePrimitive;
import ru.windcorp.progressia.client.graphics.texture.TextureSettings;
import ru.windcorp.progressia.common.resource.ResourceManager;

public class TestTypeface extends SpriteTypeface {
	
	private static final TCharObjectMap<Texture> TEXTURES = new TCharObjectHashMap<>();
	
	public TestTypeface() {
		super("Test", 8, 1);
		
		TexturePrimitive primitive = null;
		try {
			primitive = new TexturePrimitive(TextureLoader.loadPixels(
					ResourceManager.getTextureResource("font_test"),
					new TextureSettings(false)
			).toStatic());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		Vec2 size = new Vec2(0.5f, 0.5f);
		
		TEXTURES.put('A', new SimpleTexture(new Sprite(primitive, new Vec2(0, 0.5f), size)));
		TEXTURES.put('B', new SimpleTexture(new Sprite(primitive, new Vec2(0.5f, 0.5f), size)));
		TEXTURES.put('C', new SimpleTexture(new Sprite(primitive, new Vec2(0, 0), size)));
		TEXTURES.put('D', new SimpleTexture(new Sprite(primitive, new Vec2(0.5f, 0), size)));
	}
	
	@Override
	public Texture getTexture(char c) {
		return TEXTURES.get(c);
	}

	@Override
	public boolean supports(char c) {
		return TEXTURES.containsKey(c);
	}
	
	@Override
	public ShapeRenderProgram getProgram() {
		return FlatRenderProgram.getDefault();
	}

}
