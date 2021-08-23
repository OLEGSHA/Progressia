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

package ru.windcorp.progressia.client.graphics.font;

import gnu.trove.map.TCharObjectMap;
import gnu.trove.set.TIntSet;
import gnu.trove.set.hash.TIntHashSet;
import ru.windcorp.progressia.client.graphics.flat.FlatRenderProgram;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram;
import ru.windcorp.progressia.client.graphics.texture.Texture;

public class GNUUnifont extends SpriteTypeface {

	public static final int HEIGHT = 16;
	public static final TIntSet WIDTHS = new TIntHashSet(new int[] { 8, 16 });

	private final TCharObjectMap<Texture> textures;

	public GNUUnifont(TCharObjectMap<Texture> textures) {
		super("GNUUnifont", HEIGHT, 1);
		this.textures = textures;
	}

	@Override
	public Texture getTexture(char c) {
		if (!supports(c))
			return textures.get('?');
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
