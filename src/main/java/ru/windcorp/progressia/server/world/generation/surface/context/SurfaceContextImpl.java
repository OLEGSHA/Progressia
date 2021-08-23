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

import java.util.Random;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.server.world.context.ServerTileContext;
import ru.windcorp.progressia.server.world.context.impl.RotatingServerContext;
import ru.windcorp.progressia.server.world.generation.surface.Surface;

public class SurfaceContextImpl extends RotatingServerContext implements SurfaceTileContext {

	final Surface surface;
	final Vec3i min = new Vec3i();
	final Vec3i max = new Vec3i();
	private Random random;

	private final SurfaceContextImplLogic logic;

	public SurfaceContextImpl(ServerTileContext parent, Surface surface) {
		super(parent, surface.getUp());
		this.logic = new SurfaceContextImplLogic(this);
		
		this.surface = surface;
	}

	@Override
	protected void transform(Vec3i userLocation, Vec3i output) {
		output.set(userLocation.x, userLocation.y, userLocation.z);
		output.z += surface.getSeaLevel();
		super.transform(output, output);
	}

	@Override
	protected void untransform(Vec3i parentLocation, Vec3i output) {
		super.untransform(parentLocation, output);
		output.z -= surface.getSeaLevel();
	}
	
	@Override
	public Surface getSurface() {
		return surface;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @implNote can rw
	 */
	@Override
	public Vec3i getMin() {
		return min;
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @implNote can rw
	 */
	@Override
	public Vec3i getMax() {
		return max;
	}
	
	@Override
	public Random getRandom() {
		return random;
	}
	
	public void setRandom(Random random) {
		this.random = random;
	}

	@Override
	public SurfaceContextImplLogic logic() {
		return logic;
	}

	@Override
	public SurfaceTileContext push(Vec3i location) {
		super.push(location);
		return this;
	}

	@Override
	public SurfaceTileContext push(Vec3i location, RelFace face) {
		super.push(location, face);
		return this;
	}

	@Override
	public SurfaceTileContext push(Vec3i location, RelFace face, int layer) {
		super.push(location, face, layer);
		return this;
	}

}
