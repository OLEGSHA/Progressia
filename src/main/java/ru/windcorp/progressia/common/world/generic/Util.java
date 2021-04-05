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
package ru.windcorp.progressia.common.world.generic;

import java.util.function.Predicate;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.Coordinates;

class Util {
	
	public static int getBorderHits(Vec3i blockInChunk) {
		int hits = 0;
		
		if (Coordinates.isOnChunkBorder(blockInChunk.x)) hits++;
		if (Coordinates.isOnChunkBorder(blockInChunk.y)) hits++;
		if (Coordinates.isOnChunkBorder(blockInChunk.z)) hits++;
		
		return hits;
	}
	
	public static boolean testBiC(Vec3i blockInWorld, GenericChunk<?, ?, ?, ?> chunk, Predicate<Vec3i> test) {
		Vec3i v = Vectors.grab3i();

		v = Coordinates.getInWorld(chunk.getPosition(), Vectors.ZERO_3i, v);
		v = blockInWorld.sub(v, v);

		boolean result = test.test(v);

		Vectors.release(v);
		
		return result;
	}
	
	

}
