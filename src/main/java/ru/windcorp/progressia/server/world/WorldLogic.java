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
package ru.windcorp.progressia.server.world;

import java.util.Collection;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.rels.BlockFace;

public interface WorldLogic extends WorldLogicRO {

	/*
	 * Override return types
	 */
	
	@Override
	WorldData getData();

	@Override
	ChunkLogic getChunk(Vec3i pos);

	@Override
	Collection<? extends ChunkLogic> getChunks();

	@Override
	default ChunkLogic getChunkByBlock(Vec3i blockInWorld) {
		return (ChunkLogic) WorldLogicRO.super.getChunkByBlock(blockInWorld);
	}

	@Override
	default TileLogicStack getTiles(Vec3i blockInWorld, BlockFace face) {
		return (TileLogicStack) WorldLogicRO.super.getTiles(blockInWorld, face);
	}

	@Override
	default TileLogicStack getTilesOrNull(Vec3i blockInWorld, BlockFace face) {
		return (TileLogicStack) WorldLogicRO.super.getTilesOrNull(blockInWorld, face);
	}

}
