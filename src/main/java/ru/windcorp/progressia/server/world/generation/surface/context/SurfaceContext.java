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
package ru.windcorp.progressia.server.world.generation.surface.context;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.server.world.generation.surface.Surface;

public interface SurfaceContext {

	/**
	 * Returns the {@link Surface} object relevant to this context.
	 * 
	 * @return the surface details
	 */
	Surface getSurface();

	/**
	 * Returns lower bounds (inclusive) on the coordinates of the requested
	 * region.
	 * 
	 * @return the coordinates of the minimum corner of the region that
	 *         should be processed
	 */
	Vec3i getMin();

	/**
	 * Returns upper bounds (inclusive) on the coordinates of the requested
	 * region.
	 * 
	 * @return the coordinates of the maximum corner of the region that
	 *         should be processed
	 */
	Vec3i getMax();

	/*
	 * Convenience methods
	 */

	default int getMinX() {
		return getMin().x;
	}

	default int getMinY() {
		return getMin().y;
	}

	default int getMinZ() {
		return getMin().z;
	}

	default int getMaxX() {
		return getMax().x;
	}

	default int getMaxY() {
		return getMax().y;
	}

	default int getMaxZ() {
		return getMax().z;
	}

	default boolean isRequested(int x, int y, int z) {
		Vec3i min = getMin();
		Vec3i max = getMax();
		return (min.x <= x && x <= max.x) && (min.y <= y && y <= max.y) && (min.z <= z && z <= max.z);
	}
	
	default boolean isRequested(Vec3i location) {
		return isRequested(location.x, location.y, location.z);
	}
	
	default void forEachRequested(Consumer<? super Vec3i> action) {
		Vec3i min = getMin();
		Vec3i max = getMax();
		VectorUtil.iterateCuboid(
			min.x,
			min.y,
			min.z,
			max.x + 1,
			max.y + 1,
			max.z + 1,
			action
		);
	}
	
	/**
	 * Provided vectors have z set to {@link #getMinZ()}.
	 */
	default void forEachOnFloor(Consumer<? super Vec3i> action) {
		forEachOnLayer(action, getMinZ());
	}
	
	/**
	 * Provided vectors have z set to {@link #getMaxZ()}.
	 */
	default void forEachOnCeiling(Consumer<? super Vec3i> action) {
		forEachOnLayer(action, getMaxZ());
	}
	
	/**
	 * Provided vectors have z set to layer.
	 */
	default void forEachOnLayer(Consumer<? super Vec3i> action, int layer) {
		Vec3i min = getMin();
		Vec3i max = getMax();
		VectorUtil.iterateCuboid(
			min.x,
			min.y,
			layer,
			max.x + 1,
			max.y + 1,
			layer + 1,
			action
		);
	}

}
