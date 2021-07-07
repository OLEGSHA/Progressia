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

import java.util.function.Consumer;
import java.util.function.Predicate;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.AxisRotations;

public class GenericChunks {
	
	public static Vec3i resolve(Vec3i relativeCoords, AbsFace up, Vec3i output) {
		if (output == null) {
			output = new Vec3i();
		}

		final int offset = GenericChunk.BLOCKS_PER_CHUNK - 1;

		output.set(relativeCoords.x, relativeCoords.y, relativeCoords.z);
		output.mul(2).sub(offset);

		AxisRotations.resolve(output, up, output);

		output.add(offset).div(2);

		return output;
	}
	
	public static Vec3i relativize(Vec3i absoluteCoords, AbsFace up, Vec3i output) {
		if (output == null) {
			output = new Vec3i();
		}

		final int offset = GenericChunk.BLOCKS_PER_CHUNK - 1;

		output.set(absoluteCoords.x, absoluteCoords.y, absoluteCoords.z);
		output.mul(2).sub(offset);

		AxisRotations.relativize(output, up, output);

		output.add(offset).div(2);

		return output;
	}
	
	private static int getBorderHits(Vec3i blockInChunk) {
		int hits = 0;
		
		if (Coordinates.isOnChunkBorder(blockInChunk.x)) hits++;
		if (Coordinates.isOnChunkBorder(blockInChunk.y)) hits++;
		if (Coordinates.isOnChunkBorder(blockInChunk.z)) hits++;
		
		return hits;
	}
	
	static boolean testBiC(Vec3i blockInWorld, GenericChunk<?, ?, ?, ?, ?> chunk, Predicate<Vec3i> test) {
		Vec3i v = Vectors.grab3i();

		v = Coordinates.getInWorld(chunk.getPosition(), Vectors.ZERO_3i, v);
		v = blockInWorld.sub(v, v);

		boolean result = test.test(v);

		Vectors.release(v);
		
		return result;
	}

	public static boolean containsBiC(Vec3i blockInChunk) {
		return blockInChunk.x >= 0 && blockInChunk.x < GenericChunk.BLOCKS_PER_CHUNK &&
			blockInChunk.y >= 0 && blockInChunk.y < GenericChunk.BLOCKS_PER_CHUNK &&
			blockInChunk.z >= 0 && blockInChunk.z < GenericChunk.BLOCKS_PER_CHUNK;
	}

	public static boolean isSurfaceBiC(Vec3i blockInChunk) {
		return GenericChunks.getBorderHits(blockInChunk) >= 1;
	}

	public static boolean isEdgeBiC(Vec3i blockInChunk) {
		return GenericChunks.getBorderHits(blockInChunk) >= 2;
	}

	public static boolean isVertexBiC(Vec3i blockInChunk) {
		return GenericChunks.getBorderHits(blockInChunk) == 3;
	}

	public static void forEachBiC(Consumer<? super Vec3i> action) {
		VectorUtil.iterateCuboid(
			0,
			0,
			0,
			GenericChunk.BLOCKS_PER_CHUNK,
			GenericChunk.BLOCKS_PER_CHUNK,
			GenericChunk.BLOCKS_PER_CHUNK,
			action
		);
	}

}
