package ru.windcorp.progressia.client.graphics.font;

import gnu.trove.map.TCharObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import ru.windcorp.progressia.client.graphics.flat.FlatRenderProgram;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram;
import ru.windcorp.progressia.client.graphics.texture.Texture;

public class GNUUnifont extends SpriteTypeface {
	
	public static final int HEIGHT = 16;
	public static final TIntSet WIDTHS = new TIntHashSet(new int[] {8, 16});
	
	private final TCharObjectMap<Texture> textures;

	public GNUUnifont(TCharObjectMap<Texture> textures) {
		super("GNUUnifont", HEIGHT, 1);
		this.textures = textures;
	}

	@Override
	public Texture getTexture(char c) {
		if (!supports(c)) return textures.get('?');
		return textures.get(c);
	}

	@Override
	public ShapeRenderProgram getProgram() {
		return FlatRenderProgram.getDefault();
	}

	@Override
	public boolean supports(char c) {
		return textures.containsKey(c);
	}
	
	@Override
	public float getItalicsSlant() {
		return 3 * super.getItalicsSlant();
	}

}
