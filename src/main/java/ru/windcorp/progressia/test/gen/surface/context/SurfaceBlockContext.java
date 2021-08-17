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
package ru.windcorp.progressia.test.gen.surface.context;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.rels.RelFace;
import ru.windcorp.progressia.common.world.rels.RelRelation;
import ru.windcorp.progressia.server.world.context.ServerBlockContext;

public interface SurfaceBlockContext extends ServerBlockContext, SurfaceWorldContext {

	public interface Logic extends ServerBlockContext.Logic, SurfaceWorldContext.Logic {

		@Override
		SurfaceBlockContext data();
		
		@Override
		default SurfaceBlockContext.Logic pushRelative(int dx, int dy, int dz) {
			return push(getLocation().add_(dx, dy, dz));
		}
		
		@Override
		default SurfaceBlockContext.Logic pushRelative(Vec3i direction) {
			return push(getLocation().add_(direction));
		}
		
		@Override
		default SurfaceBlockContext.Logic pushRelative(RelRelation direction) {
			return push(getLocation().add_(direction.getRelVector()));
		}
		
		@Override
		default SurfaceTileStackContext.Logic push(RelFace face) {
			return push(getLocation(), face);
		}
		
		@Override
		default SurfaceTileContext.Logic push(RelFace face, int layer) {
			return push(getLocation(), face, layer);
		}

	}

	@Override
	SurfaceBlockContext.Logic logic();
	
	@Override
	default SurfaceBlockContext pushRelative(int dx, int dy, int dz) {
		return push(getLocation().add_(dx, dy, dz));
	}
	
	@Override
	default SurfaceBlockContext pushRelative(Vec3i direction) {
		return push(getLocation().add_(direction));
	}
	
	@Override
	default SurfaceBlockContext pushRelative(RelRelation direction) {
		return push(getLocation().add_(direction.getRelVector()));
	}
	
	@Override
	default SurfaceTileStackContext push(RelFace face) {
		return push(getLocation(), face);
	}
	
	@Override
	default SurfaceTileContext push(RelFace face, int layer) {
		return push(getLocation(), face, layer);
	}

}
