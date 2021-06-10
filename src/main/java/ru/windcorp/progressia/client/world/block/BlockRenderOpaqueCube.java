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

package ru.windcorp.progressia.client.world.block;

import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class BlockRenderOpaqueCube extends BlockRenderTexturedCube {

	public BlockRenderOpaqueCube(String id, Texture topTexture, Texture bottomTexture, Texture northTexture,
			Texture southTexture, Texture eastTexture, Texture westTexture) {
		super(id, topTexture, bottomTexture, northTexture, southTexture, eastTexture, westTexture);
	}

	public BlockRenderOpaqueCube(String id, Texture texture) {
		this(id, texture, texture, texture, texture, texture, texture);
	}

	@Override
	public boolean isOpaque(BlockFace face) {
		return true;
	}

	@Override
	public boolean isBlockOpaque() {
		return true;
	}

}
