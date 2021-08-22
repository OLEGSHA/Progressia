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

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.server.world.context.impl.DefaultServerContextLogic;
import ru.windcorp.progressia.server.world.generation.surface.Surface;

public class SurfaceContextImplLogic extends DefaultServerContextLogic implements SurfaceTileContext.Logic {

	private final SurfaceContextImpl surfaceParent;

	public SurfaceContextImplLogic(SurfaceContextImpl surfaceParent) {
		super(surfaceParent);
		this.surfaceParent = surfaceParent;
	}
	
	@Override
	public Surface getSurface() {
		return this.surfaceParent.surface;
	}

	@Override
	public Vec3i getMin() {
		return this.surfaceParent.min;
	}

	@Override
	public Vec3i getMax() {
		return this.surfaceParent.max;
	}

	@Override
	public SurfaceContextImpl data() {
		return this.surfaceParent;
	}

	@Override
	public SurfaceTileContext.Logic push(Vec3i location) {
		super.push(location);
		return this;
	}

	@Override
	public SurfaceTileContext.Logic push(Vec3i location, RelFace face) {
		super.push(location, face);
		return this;
	}

	@Override
	public SurfaceTileContext.Logic push(Vec3i location, RelFace face, int layer) {
		super.push(location, face, layer);
		return this;
	}

}