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

import ru.windcorp.progressia.client.graphics.flat.FlatRenderProgram;
import ru.windcorp.progressia.client.graphics.font.SpriteTypeface;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderProgram;
import ru.windcorp.progressia.client.graphics.texture.ComplexTexture;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.texture.TextureLoader;
import ru.windcorp.progressia.client.graphics.texture.TexturePrimitive;
import ru.windcorp.progressia.client.graphics.texture.TextureSettings;
import ru.windcorp.progressia.common.resource.ResourceManager;

public class ItemAmountTypeface extends SpriteTypeface {
	
	public static final int HEIGHT = 5;
	public static final int WIDTH = 3;

	private final Texture[] textures = new Texture[10];

	public ItemAmountTypeface() throws IOException {
		super("ItemAmount", HEIGHT, 1);
		
		ComplexTexture atlas = new ComplexTexture(new TexturePrimitive(
			TextureLoader.loadPixels(
				ResourceManager.getTextureResource("gui/ItemAmountTypeface"),
				new TextureSettings(false)
			).getData()
		), 30, 5);
		
		for (int i = 0; i <= 9; ++i) {
			textures[i] = atlas.get(i * WIDTH, 0, WIDTH, HEIGHT);
		}
	}

	@Override
	public Texture getTexture(char c) {
		if (!supports(c))
			return textures[0];
		return textures[c - '0'];
	}

	@Override
	public ShapeRenderProgram getProgram() {
		return FlatRenderProgram.getDefault();
	}

	@Override
	public boolean supports(char c) {
		return c >= '0' && c <= '9';
	}

}
