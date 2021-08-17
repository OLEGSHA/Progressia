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

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.test.gen.surface.context.SurfaceBlockContext;
import ru.windcorp.progressia.test.gen.surface.context.SurfaceWorldContext;

public abstract class SurfaceTopLayerFeature extends SurfaceFeature {

	public SurfaceTopLayerFeature(String id) {
		super(id);
	}

	protected abstract void processTopBlock(SurfaceBlockContext context);

	protected abstract boolean isSolid(SurfaceBlockContext context);

	@Override
	public void process(SurfaceWorldContext context) {
		Vec3i cursor = new Vec3i();

		context.forEachOnFloor(pos -> {

			cursor.set(pos.x, pos.y, pos.z);

			if (!isSolid(context.push(cursor))) {
				context.pop();
				return;
			}
			context.pop();

			for (cursor.z += 1; cursor.z <= context.getMaxZ() + 1; ++cursor.z) {
				SurfaceBlockContext blockContext = context.push(cursor);

				if (!isSolid(blockContext)) {
					processTopBlock(blockContext.pushRelative(0, 0, -1));
					context.pop();
					context.pop();
					return;
				}

				context.pop();
			}

		});
	}

}
