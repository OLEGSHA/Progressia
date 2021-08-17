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

import glm.Glm;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.generic.GenericChunks;
import ru.windcorp.progressia.test.gen.surface.context.SurfaceWorldContext;

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

	public abstract void process(SurfaceWorldContext context);

}
