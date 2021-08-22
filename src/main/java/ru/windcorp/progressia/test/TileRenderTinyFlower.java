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

import glm.mat._4.Mat4;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.rels.RelFace;

public class TileRenderTinyFlower extends TileRenderHerb {
	
	private final float size;

	public TileRenderTinyFlower(String id, Texture texture, int maxCount, float size) {
		super(id, texture, maxCount);
		this.size = size;
	}
	
	@Override
	protected boolean getTransform(
		DefaultChunkData chunk,
		Vec3i relBlockInChunk,
		RelFace blockFace,
		int count,
		Mat4 output
	) {
		boolean result = super.getTransform(chunk, relBlockInChunk, blockFace, count, output);
		output.scale(size);
		return result;
	}

}
