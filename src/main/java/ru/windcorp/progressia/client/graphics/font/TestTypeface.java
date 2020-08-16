package ru.windcorp.progressia.client.graphics.font;

import java.io.IOException;

import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import gnu.trove.map.TCharObjectMap;
import gnu.trove.map.hash.TCharObjectHashMap;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.flat.FlatRenderProgram;
import ru.windcorp.progressia.client.graphics.model.Face;
import ru.windcorp.progressia.client.graphics.model.Faces;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.WorldRenderable;
import ru.windcorp.progressia.client.graphics.texture.SimpleTexture;
import ru.windcorp.progressia.client.graphics.texture.Sprite;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.texture.TextureLoader;
import ru.windcorp.progressia.client.graphics.texture.TexturePrimitive;
import ru.windcorp.progressia.client.graphics.texture.TextureSettings;
import ru.windcorp.progressia.common.resource.ResourceManager;

public class TestTypeface extends Typeface {
	
	private static final TCharObjectMap<Texture> TEXTURES = new TCharObjectHashMap<>();
	
	private static final Vec3 SIZE = new Vec3(8, 8, 0);
	
	public TestTypeface() {
		super("Test");
		
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
	public WorldRenderable assemble(
			CharSequence chars, int style,
			float align, int maxWidth,
			int color
	) {
		Face[] faces = new Face[chars.length()];
		
		Vec3 caret = new Vec3();
		
		for (int i = 0; i < chars.length(); ++i) {
			char c = chars.charAt(i);
			
			if (supports(c)) {
				faces[i] = Faces.createRectangle(
						FlatRenderProgram.getDefault(),
						TEXTURES.get(c), new Vec3(1, 1, 1),
						caret, new Vec3(SIZE.x, 0, 0), new Vec3(0, SIZE.y, 0)
				);
			}
			
			caret.x += SIZE.x;
		}
		
		return new Shape(Usage.STATIC, FlatRenderProgram.getDefault(), faces);
	}

	@Override
	protected long getSize(
			CharSequence chars, int style,
			float align, int maxWidth
	) {
		return pack(
				(int) (chars.length() * SIZE.x),
				(int) (SIZE.y)
		);
	}

	@Override
	public boolean supports(char c) {
		return TEXTURES.containsKey(c);
	}

}
