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
package ru.windcorp.progressia.server.world.context;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.context.WorldDataContext;
import ru.windcorp.progressia.common.world.rels.RelFace;

public interface ServerWorldContext extends WorldDataContext, ServerWorldContextRO {

	public interface Logic extends ServerWorldContextRO.Logic {

		@Override
		ServerWorldContext data();
		
		@Override
		ServerBlockContext.Logic push(Vec3i location);
		
		@Override
		ServerTileStackContext.Logic push(Vec3i location, RelFace face);
		
		@Override
		ServerTileContext.Logic push(Vec3i location, RelFace face, int layer);

	}

	@Override
	ServerWorldContext.Logic logic();
	
	@Override
	ServerBlockContext push(Vec3i location);
	
	@Override
	ServerTileStackContext push(Vec3i location, RelFace face);
	
	@Override
	ServerTileContext push(Vec3i location, RelFace face, int layer);

}