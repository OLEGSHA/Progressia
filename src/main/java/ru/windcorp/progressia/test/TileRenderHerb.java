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
import ru.windcorp.progressia.client.world.tile.TileRenderCross;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.rels.RelFace;

public class TileRenderHerb extends TileRenderCross {
	
	/*
	 * Stolen from OpenJDK's Random implementation
	 * *evil cackling*
	 */
	private static final long MULTIPLIER = 0x5DEECE66DL;
	private static final long ADDEND = 0xBL;
	private static final long MASK = (1L << 48) - 1;
	private static final double DOUBLE_UNIT = 0x1.0p-53; // 1.0 / (1L << 53)

	private static long permute(long seed) {
		return (seed * MULTIPLIER + ADDEND) & MASK;
	}

	private static double getDouble(long seed) {
		final int mask26bits = (1 << 26) - 1;
		final int mask27bits = (1 << 27) - 1;

		int randomBitsX26 = (int) (seed & 0xFFFFFFFF);
		int randomBitsX27 = (int) ((seed >>> Integer.SIZE) & 0xFFFFFFFF);

		randomBitsX26 = randomBitsX26 & mask26bits;
		randomBitsX27 = randomBitsX27 & mask27bits;

		return (((long) (randomBitsX26) << 27) + randomBitsX27) * DOUBLE_UNIT;
	}
	
	private final int maxCount;

	public TileRenderHerb(String id, Texture texture, int maxCount) {
		super(id, texture, true);
		this.maxCount = maxCount;
	}
	
	@Override
	protected boolean getTransform(
		DefaultChunkData chunk,
		Vec3i relBlockInChunk,
		RelFace blockFace,
		int count,
		Mat4 output
	) {
		
		long seed = permute(count ^ getId().hashCode());
		seed = permute(seed + relBlockInChunk.x);
		seed = permute(seed + relBlockInChunk.y);
		seed = permute(seed + relBlockInChunk.z);
		seed = permute(seed + blockFace.getId());
		
		float x = (float) getDouble(seed) * 0.8f - 0.4f;
		seed = permute(seed);
		float y = (float) getDouble(seed) * 0.8f - 0.4f;
		seed = permute(seed);
		float size = (float) getDouble(seed) * 0.5f + 0.5f;
		seed = permute(seed);
		double rotation = getDouble(seed) * Math.PI / 8;
		
		output.identity().translate(x, y, 0).scale(size).rotateZ(rotation);
		return (count == 0) || ((count < maxCount) && (seed % 3 != 0));
	}

}
