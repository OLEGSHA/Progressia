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

package ru.windcorp.progressia.client.graphics.texture;

import java.util.Map;

import glm.vec._2.Vec2;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class ComplexTexture {

	private final TexturePrimitive primitive;

	private final float assumedWidth;
	private final float assumedHeight;

	public ComplexTexture(TexturePrimitive primitive, int abstractWidth, int abstractHeight) {
		this.primitive = primitive;

		this.assumedWidth = abstractWidth / (float) primitive.getWidth() * primitive.getBufferWidth();

		this.assumedHeight = abstractHeight / (float) primitive.getHeight() * primitive.getBufferHeight();
	}

	public Texture get(int x, int y, int width, int height) {
		return new SimpleTexture(new Sprite(primitive, new Vec2(x / assumedWidth, y / assumedHeight),
				new Vec2(width / assumedWidth, height / assumedHeight)));
	}

	public Map<BlockFace, Texture> getCuboidTextures(int x, int y, int width, int height, int depth) {
		return BlockFace.mapToFaces(get(x + depth + width, y + height + depth, -width, -depth),
				get(x + depth + width + width, y + height + depth, -width, -depth), get(x + depth, y, width, height),
				get(x + depth + width + depth, y, width, height), get(x, y, depth, height),
				get(x + depth + width, y, depth, height));
	}

	public Map<BlockFace, Texture> getCuboidTextures(int x, int y, int size) {
		return getCuboidTextures(x, y, size, size, size);
	}

}
