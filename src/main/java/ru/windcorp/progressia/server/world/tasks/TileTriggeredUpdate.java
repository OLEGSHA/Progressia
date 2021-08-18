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

package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.TickAndUpdateUtil;
import ru.windcorp.progressia.server.world.WorldLogic;

class TileTriggeredUpdate extends CachedEvaluation {

	private final Vec3i blockInWorld = new Vec3i();
	private BlockFace face = null;

	public TileTriggeredUpdate(Consumer<? super CachedEvaluation> disposer) {
		super(disposer);
	}

	@Override
	public void evaluate(Server server) {
		Vec3i cursor = new Vec3i(blockInWorld.x, blockInWorld.y, blockInWorld.z);

		WorldLogic world = server.getWorld();

		TickAndUpdateUtil.updateTiles(world, cursor, face); // Update facemates
															// (also self)
		TickAndUpdateUtil.updateBlock(world, cursor); // Update block on one
														// side
		cursor.add(face.getVector());
		TickAndUpdateUtil.updateBlock(world, cursor); // Update block on the
														// other side
		TickAndUpdateUtil.updateTiles(world, cursor, face.getCounter()); // Update
																			// complement
	}

	public void init(Vec3i blockInWorld, BlockFace face) {
		this.blockInWorld.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
		this.face = face;
	}

	@Override
	public void getRelevantChunk(Vec3i output) {
		Coordinates.convertInWorldToChunk(blockInWorld, output);
	}

}
