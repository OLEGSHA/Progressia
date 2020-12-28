/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.common.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import glm.vec._3.i.Vec3i;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.ChunkMap;
import ru.windcorp.progressia.common.world.generic.ChunkSet;
import ru.windcorp.progressia.common.world.generic.GenericWorld;
import ru.windcorp.progressia.common.world.generic.LongBasedChunkMap;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataStack;
import ru.windcorp.progressia.test.TestContent;

public class WorldData
implements GenericWorld<
	BlockData,
	TileData,
	TileDataStack,
	ChunkData,
	EntityData
> {

	private final ChunkMap<ChunkData> chunksByPos = new LongBasedChunkMap<>(
			new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>(), this)
	);
	
	private final Collection<ChunkData> chunks =
			Collections.unmodifiableCollection(chunksByPos.values());
	
	private final TLongObjectMap<EntityData> entitiesById =
			new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>(), this);
	
	private final Collection<EntityData> entities =
			Collections.unmodifiableCollection(entitiesById.valueCollection());
	
	private float time = 0;
	
	private final Collection<WorldDataListener> listeners =
			Collections.synchronizedCollection(new ArrayList<>());
	
	public WorldData() {
		
	}
	
	@Override
	public ChunkData getChunk(Vec3i pos) {
		return chunksByPos.get(pos);
	}
	
	@Override
	public Collection<ChunkData> getChunks() {
		return chunks;
	}
	
	public ChunkSet getLoadedChunks() {
		return chunksByPos.keys();
	}
	
	@Override
	public Collection<EntityData> getEntities() {
		return entities;
	}
	
	public void tmp_generate() {
		final int size = 1;
		Vec3i cursor = new Vec3i(0, 0, 0);
		
		for (cursor.x = -(size / 2); cursor.x <= (size / 2); ++cursor.x) {
			for (cursor.y = -(size / 2); cursor.y <= (size / 2); ++cursor.y) {
				for (cursor.z = -(size / 2); cursor.z <= (size / 2); ++cursor.z) {
					ChunkData chunk = new ChunkData(cursor, this);
					TestContent.generateChunk(chunk);
					addChunk(chunk);
				}
			}
		}
	}
	
	private void addChunkListeners(ChunkData chunk) {
		getListeners().forEach(l -> l.getChunkListeners(this, chunk.getPosition(), chunk::addListener));
	}
	
	public synchronized void addChunk(ChunkData chunk) {
		addChunkListeners(chunk);
		
		ChunkData previous = chunksByPos.get(chunk);
		if (previous != null) {
			throw new IllegalArgumentException(String.format(
					"Chunk at (%d; %d; %d) already exists",
					chunk.getPosition().x, chunk.getPosition().y, chunk.getPosition().z
			));
		}
		
		chunksByPos.put(chunk, chunk);
		
		chunk.forEachEntity(entity ->
			entitiesById.put(entity.getEntityId(), entity)
		);
		
		chunk.onLoaded();
		getListeners().forEach(l -> l.onChunkLoaded(this, chunk));
	}
	
	public synchronized void removeChunk(ChunkData chunk) {
		getListeners().forEach(l -> l.beforeChunkUnloaded(this, chunk));
		chunk.beforeUnloaded();
		
		chunk.forEachEntity(entity ->
			entitiesById.remove(entity.getEntityId())
		);
		
		chunksByPos.remove(chunk);
	}
	
	public void setBlock(Vec3i blockInWorld, BlockData block, boolean notify) {
		ChunkData chunk = getChunkByBlock(blockInWorld);
		if (chunk == null)
			throw new IllegalCoordinatesException(
					"Coordinates "
					+ "(" + blockInWorld.x + "; " + blockInWorld.y + "; " + blockInWorld.z + ") "
					+ "do not belong to a loaded chunk"
			);
		
		chunk.setBlock(Coordinates.convertInWorldToInChunk(blockInWorld, null), block, notify);
	}
	
	public EntityData getEntity(long entityId) {
		return entitiesById.get(entityId);
	}
	
	public float getTime() {
		return time;
	}
	
	public void advanceTime(float change) {
		this.time += change;
	}
	
	public CollisionModel getCollisionModelOfBlock(Vec3i blockInWorld) {
		ChunkData chunk = getChunkByBlock(blockInWorld);
		if (chunk == null) return null;
		
		BlockData block = chunk.getBlock(Coordinates.convertInWorldToInChunk(blockInWorld, null));
		if (block == null) return null;
		return block.getCollisionModel();
	}
	
	public Collection<WorldDataListener> getListeners() {
		return listeners;
	}

	public void addListener(WorldDataListener e) {
		listeners.add(e);
	}

	public void removeListener(WorldDataListener o) {
		listeners.remove(o);
	}
	
}
