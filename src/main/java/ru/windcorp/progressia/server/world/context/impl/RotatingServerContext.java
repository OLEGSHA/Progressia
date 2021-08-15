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
package ru.windcorp.progressia.server.world.context.impl;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.AxisRotations;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.server.world.context.ServerTileContext;

public class RotatingServerContext extends TransformingServerContext {
	
	private final AbsFace up;

	public RotatingServerContext(ServerTileContext parent, AbsFace up) {
		super(parent);
		this.up = up;
	}
	
	public AbsFace getUp() {
		return up;
	}

	@Override
	protected void transform(Vec3i userLocation, Vec3i output) {
		AxisRotations.resolve(userLocation, up, output);
	}

	@Override
	protected void untransform(Vec3i parentLocation, Vec3i output) {
		AxisRotations.relativize(parentLocation, up, output);
	}

	@Override
	protected RelFace transform(RelFace userFace) {
		return userFace.resolve(up).relativize(AbsFace.POS_Z);
	}

	@Override
	protected RelFace untransform(RelFace parentFace) {
		return parentFace.resolve(AbsFace.POS_Z).relativize(up);
	}

}
