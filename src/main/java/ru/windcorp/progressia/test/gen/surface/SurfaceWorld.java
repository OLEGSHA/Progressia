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

import java.util.Collection;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.state.StateChange;
import ru.windcorp.progressia.common.state.StatefulObject;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.GravityModel;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.EntityGeneric;
import ru.windcorp.progressia.common.world.generic.GenericChunks;
import ru.windcorp.progressia.common.world.rels.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.TileDataStack;
import ru.windcorp.progressia.common.world.WorldData;

public class SurfaceWorld
	implements WorldData {

	private final Surface surface;
	private final WorldData parent;

	public SurfaceWorld(
		Surface surface,
		WorldData parent
	) {
		this.surface = surface;
		this.parent = parent;
	}

	/**
	 * @return the surface
	 */
	public Surface getSurface() {
		return surface;
	}

	/**
	 * @return the parent
	 */
	public WorldData getParent() {
		return parent;
	}

	/*
	 * Delegate methods
	 */

	@Override
	public Collection<? extends ChunkData> getChunks() {
		return parent.getChunks();
	}

	@Override
	public ChunkData getChunk(Vec3i pos) {
		return parent.getChunk(pos);
	}

	@Override
	public Collection<EntityData> getEntities() {
		return parent.getEntities();
	}

	@Override
	public EntityData getEntity(long entityId) {
		return parent.getEntity(entityId);
	}

	@Override
	public void setBlock(Vec3i blockInWorld, BlockData block, boolean notify) {
		parent.setBlock(blockInWorld, block, notify);
	}

	@Override
	public void addEntity(EntityData entity) {
		parent.addEntity(entity);
	}

	@Override
	public void removeEntity(long entityId) {
		parent.removeEntity(entityId);
	}

	public Vec3i resolve(Vec3i surfacePosition, Vec3i output) {
		if (output == null) {
			output = new Vec3i();
		}

		output.set(surfacePosition.x, surfacePosition.y, surfacePosition.z);
		output.z += getSurface().getSeaLevel();

		GenericChunks.resolve(output, getSurface().getUp(), output);

		return output;
	}
	
	public Vec3i relativize(Vec3i absolutePosition, Vec3i output) {
		if (output == null) {
			output = new Vec3i();
		}

		output.set(absolutePosition.x, absolutePosition.y, absolutePosition.z);

		GenericChunks.relativize(output, getSurface().getUp(), output);
		output.z -= getSurface().getSeaLevel();

		return output;
	}

	public BlockData getBlockSfc(Vec3i surfaceBlockInWorld) {
		Vec3i blockInWorld = Vectors.grab3i();
		resolve(surfaceBlockInWorld, blockInWorld);
		BlockData result = parent.getBlock(blockInWorld);
		Vectors.release(blockInWorld);
		return result;
	}

	public void setBlockSfc(Vec3i surfaceBlockInWorld, BlockData block, boolean notify) {
		Vec3i blockInWorld = Vectors.grab3i();
		resolve(surfaceBlockInWorld, blockInWorld);
		parent.setBlock(blockInWorld, block, notify);
		Vectors.release(blockInWorld);
	}

	public TileDataStack getTilesSfc(Vec3i surfaceBlockInWorld, BlockFace face) {
		Vec3i blockInWorld = Vectors.grab3i();
		resolve(surfaceBlockInWorld, blockInWorld);
		TileDataStack result = parent.getTiles(blockInWorld, face);
		Vectors.release(blockInWorld);
		return result;
	}

	public TileDataStack getTilesOrNullSfc(Vec3i surfaceBlockInWorld, BlockFace face) {
		Vec3i blockInWorld = Vectors.grab3i();
		resolve(surfaceBlockInWorld, blockInWorld);
		TileDataStack result = parent.getTilesOrNull(blockInWorld, face);
		Vectors.release(blockInWorld);
		return result;
	}

	public boolean hasTilesSfc(Vec3i surfaceBlockInWorld, BlockFace face) {
		Vec3i blockInWorld = Vectors.grab3i();
		resolve(surfaceBlockInWorld, blockInWorld);
		boolean result = parent.hasTiles(blockInWorld, face);
		Vectors.release(blockInWorld);
		return result;
	}

	public TileData getTileSfc(Vec3i surfaceBlockInWorld, BlockFace face, int layer) {
		Vec3i blockInWorld = Vectors.grab3i();
		resolve(surfaceBlockInWorld, blockInWorld);
		TileData result = parent.getTile(blockInWorld, face, layer);
		Vectors.release(blockInWorld);
		return result;
	}

	public boolean isBlockLoadedSfc(Vec3i surfaceBlockInWorld) {
		Vec3i blockInWorld = Vectors.grab3i();
		resolve(surfaceBlockInWorld, blockInWorld);
		boolean result = parent.isBlockLoaded(blockInWorld);
		Vectors.release(blockInWorld);
		return result;
	}

	@Override
	public float getTime() {
		return parent.getTime();
	}

	@Override
	public GravityModel getGravityModel() {
		return parent.getGravityModel();
	}

	@Override
	public <SE extends StatefulObject & EntityGeneric> void changeEntity(SE entity, StateChange<SE> change) {
		parent.changeEntity(entity, change);
	}

	@Override
	public void advanceTime(float change) {
		parent.advanceTime(change);
	}

}
