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
 
package ru.windcorp.progressia.test;

import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.world.tile.TileRenderSurface;
import ru.windcorp.progressia.common.world.rels.RelFace;

public class TestTileRenderGrass extends TileRenderSurface {

	private final Texture topTexture;
	private final Texture sideTexture;

	public TestTileRenderGrass(
		String id,
		Texture top,
		Texture side
	) {
		super(id);
		this.topTexture = top;
		this.sideTexture = side;
	}

	@Override
	public Texture getTexture(RelFace face) {
		return (face == RelFace.UP) ? topTexture : sideTexture;
	}

	@Override
	public boolean isOpaque(RelFace face) {
		return face == RelFace.UP;
	}

}
