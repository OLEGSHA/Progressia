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

package ru.windcorp.progressia.client.world;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.ChunkDataListener;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;

class ChunkUpdateListener implements ChunkDataListener {

	private final WorldRender world;

	public ChunkUpdateListener(WorldRender world) {
		this.world = world;
	}

	@Override
	public void onChunkChanged(ChunkData chunk) {
		world.getChunk(chunk).markForUpdate();
	}

	@Override
	public void onChunkLoaded(ChunkData chunk) {
		Vec3i cursor = new Vec3i();
		for (BlockFace face : BlockFace.getFaces()) {
			cursor.set(chunk.getX(), chunk.getY(), chunk.getZ());
			cursor.add(face.getVector());
			world.markChunkForUpdate(cursor);
		}
	}

	@Override
	public void onChunkBlockChanged(ChunkData chunk, Vec3i blockInChunk, BlockData previous, BlockData current) {
		onLocationChanged(chunk, blockInChunk);
	}

	@Override
	public void onChunkTilesChanged(ChunkData chunk, Vec3i blockInChunk, BlockFace face, TileData tile,
			boolean wasAdded) {
		onLocationChanged(chunk, blockInChunk);
	}

	private void onLocationChanged(ChunkData chunk, Vec3i blockInChunk) {
		Vec3i chunkPos = Vectors.grab3i().set(chunk.getX(), chunk.getY(), chunk.getZ());

		checkCoordinate(blockInChunk, chunkPos, VectorUtil.Axis.X);
		checkCoordinate(blockInChunk, chunkPos, VectorUtil.Axis.Y);
		checkCoordinate(blockInChunk, chunkPos, VectorUtil.Axis.Z);

		Vectors.release(chunkPos);
	}

	private void checkCoordinate(Vec3i blockInChunk, Vec3i chunkPos, VectorUtil.Axis axis) {
		int block = VectorUtil.get(blockInChunk, axis);
		int diff = 0;

		if (block == 0) {
			diff = -1;
		} else if (block == ChunkData.BLOCKS_PER_CHUNK - 1) {
			diff = +1;
		} else {
			return;
		}

		int previousChunkPos = VectorUtil.get(chunkPos, axis);
		VectorUtil.set(chunkPos, axis, previousChunkPos + diff);

		world.markChunkForUpdate(chunkPos);

		VectorUtil.set(chunkPos, axis, previousChunkPos);
	}

}
