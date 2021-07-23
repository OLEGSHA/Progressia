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
package ru.windcorp.progressia.test.gen.surface;

import java.util.Random;
import java.util.function.Consumer;

import glm.Glm;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.generic.GenericChunks;

public abstract class SurfaceFeature extends Namespaced {

	public static class Request {

		private final SurfaceWorld world;
		private final DefaultChunkData chunk;
		private final Vec3i minSfc = new Vec3i();
		private final Vec3i maxSfc = new Vec3i();
		
		private final Random random;

		public Request(SurfaceWorld world, DefaultChunkData chunk, Random random) {
			this.world = world;
			this.chunk = chunk;
			this.random = random;

			Vec3i tmpMin = chunk.getMinBIW(null);
			Vec3i tmpMax = chunk.getMaxBIW(null);
			
			GenericChunks.relativize(tmpMin, chunk.getUp(), tmpMin);
			GenericChunks.relativize(tmpMax, chunk.getUp(), tmpMax);

			Glm.min(tmpMin, tmpMax, minSfc);
			Glm.max(tmpMin, tmpMax, maxSfc);
			 
			minSfc.z -= world.getSurface().getSeaLevel();
			maxSfc.z -= world.getSurface().getSeaLevel();
		}

		public DefaultChunkData getChunk() {
			return chunk;
		}
		
		public SurfaceWorld getWorld() {
			return world;
		}
		
		public Random getRandom() {
			return random;
		}

		public int getMinX() {
			return minSfc.x;
		}

		public int getMaxX() {
			return maxSfc.x;
		}

		public int getMinY() {
			return minSfc.y;
		}

		public int getMaxY() {
			return maxSfc.y;
		}

		public int getMinZ() {
			return minSfc.z;
		}

		public int getMaxZ() {
			return maxSfc.z;
		}

		public Vec3i getMin() {
			return minSfc;
		}

		public Vec3i getMax() {
			return maxSfc;
		}

	}

	public SurfaceFeature(String id) {
		super(id);
	}

	public abstract void process(SurfaceWorld world, Request request);
	
	/*
	 * Utility methods
	 */

	public boolean contains(Request request, Vec3i surfaceBlockInWorld) {
		Vec3i bic = Vectors.grab3i();
		bic.set(surfaceBlockInWorld.x, surfaceBlockInWorld.y, surfaceBlockInWorld.z);
		bic.sub(request.minSfc);
		boolean result = GenericChunks.containsBiC(bic);
		Vectors.release(bic);
		return result;
	}

	public void forEach(Request request, Consumer<? super Vec3i> action) {
		VectorUtil.iterateCuboid(
			request.minSfc.x,
			request.minSfc.y,
			request.minSfc.z,
			request.maxSfc.x + 1,
			request.maxSfc.y + 1,
			request.maxSfc.z + 1,
			action
		);
	}

	/**
	 * Provided vectors have z set to {@link #getMinZ()}.
	 */
	public void forEachOnFloor(Request request, Consumer<? super Vec3i> action) {
		forEachOnLayer(request, action, request.getMinZ());
	}
	
	/**
	 * Provided vectors have z set to {@link #getMaxZ()}.
	 */
	public void forEachOnCeiling(Request request, Consumer<? super Vec3i> action) {
		forEachOnLayer(request, action, request.getMaxZ());
	}
	
	/**
	 * Provided vectors have z set to layer.
	 */
	public void forEachOnLayer(Request request, Consumer<? super Vec3i> action, int layer) {
		VectorUtil.iterateCuboid(
			request.minSfc.x,
			request.minSfc.y,
			layer,
			request.maxSfc.x + 1,
			request.maxSfc.y + 1,
			layer + 1,
			action
		);
	}

}
