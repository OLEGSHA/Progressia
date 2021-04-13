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
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.generic.GenericChunks;
import ru.windcorp.progressia.common.world.rels.AxisRotations;

public abstract class SurfaceFeature extends Namespaced {

	public static class Request {

		private final ChunkData chunk;
		private final Vec3i minSfc = new Vec3i();
		private final Vec3i maxSfc = new Vec3i();
		
		private final Random random;

		public Request(ChunkData chunk, Random random) {
			this.chunk = chunk;
			this.random = random;

			Vec3i absMin = chunk.getMinBIW(null);
			Vec3i absMax = chunk.getMaxBIW(null);

			AxisRotations.relativize(absMin, chunk.getUp(), absMin);
			AxisRotations.relativize(absMax, chunk.getUp(), absMax);

			Glm.min(absMin, absMax, minSfc);
			Glm.max(absMin, absMax, maxSfc);
		}

		public ChunkData getChunk() {
			return chunk;
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

		public boolean contains(Vec3i surfaceBlockInWorld) {
			Vec3i bic = Vectors.grab3i();
			bic.set(surfaceBlockInWorld.x, surfaceBlockInWorld.y, surfaceBlockInWorld.z);
			bic.sub(minSfc);
			boolean result = GenericChunks.containsBiC(bic);
			Vectors.release(bic);
			return result;
		}

		public void forEach(Consumer<? super Vec3i> action) {
			VectorUtil.iterateCuboid(
				minSfc.x,
				minSfc.y,
				minSfc.z,
				maxSfc.x + 1,
				maxSfc.y + 1,
				maxSfc.z + 1,
				action
			);
		}

		/**
		 * Provided vectors have z set to {@link #getMinZ()}.
		 */
		public void forEachOnFloor(Consumer<? super Vec3i> action) {
			forEachOnLayer(action, getMinZ());
		}
		
		/**
		 * Provided vectors have z set to {@link #getMaxZ()}.
		 */
		public void forEachOnCeiling(Consumer<? super Vec3i> action) {
			forEachOnLayer(action, getMaxZ());
		}
		
		/**
		 * Provided vectors have z set to layer.
		 */
		public void forEachOnLayer(Consumer<? super Vec3i> action, int layer) {
			VectorUtil.iterateCuboid(
				minSfc.x,
				minSfc.y,
				layer,
				maxSfc.x + 1,
				maxSfc.y + 1,
				layer + 1,
				action
			);
		}

	}

	public SurfaceFeature(String id) {
		super(id);
	}

	public abstract void process(SurfaceWorld world, Request request);

}
