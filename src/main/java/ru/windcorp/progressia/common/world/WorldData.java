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
import ru.windcorp.progressia.common.util.CoordinatePacker;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.test.TestContent;

public class WorldData {

	private final TLongObjectMap<ChunkData> chunksByPos =
			new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>(), this);
	
	private final Collection<ChunkData> chunks =
			Collections.unmodifiableCollection(chunksByPos.valueCollection());
	
	private final TLongObjectMap<EntityData> entitiesById =
			new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>(), this);
	
	private final Collection<EntityData> entities =
			Collections.unmodifiableCollection(entitiesById.valueCollection());
	
	private float time = 0;
	
	private final Collection<WorldDataListener> listeners =
			Collections.synchronizedCollection(new ArrayList<>());
	
	public WorldData() {
		
	}
	
	public void tmp_generate() {
		final int size = 6;
		Vec3i cursor = new Vec3i(0, 0, 0);
		
		for (cursor.x = -(size / 2); cursor.x <= (size / 2); ++cursor.x) {
			for (cursor.y = -(size / 2); cursor.y <= (size / 2); ++cursor.y) {
				ChunkData chunk = new ChunkData(cursor, this);
				TestContent.generateChunk(chunk);
				addChunkListeners(chunk);
				addChunk(chunk);
			}
		}
	}
	
	private void addChunkListeners(ChunkData chunk) {
		getListeners().forEach(l -> l.getChunkListeners(this, chunk.getPosition(), chunk::addListener));
	}
	
	private synchronized void addChunk(ChunkData chunk) {
		chunksByPos.put(getChunkKey(chunk), chunk);
		
		chunk.forEachEntity(entity ->
			entitiesById.put(entity.getEntityId(), entity)
		);
		
		chunk.onLoaded();
		getListeners().forEach(l -> l.onChunkLoaded(this, chunk));
	}
	
//	private synchronized void removeChunk(ChunkData chunk) {
//		getListeners().forEach(l -> l.beforeChunkUnloaded(this, chunk));
//		chunk.beforeUnloaded();
//		
//		chunk.forEachEntity(entity ->
//			entitiesById.remove(entity.getEntityId())
//		);
//		
//		chunksByPos.remove(getChunkKey(chunk));
//	}
	
	private static long getChunkKey(ChunkData chunk) {
		return CoordinatePacker.pack3IntsIntoLong(chunk.getPosition());
	}
	
	public ChunkData getChunk(Vec3i pos) {
		return chunksByPos.get(CoordinatePacker.pack3IntsIntoLong(pos));
	}
	
	public ChunkData getChunkByBlock(Vec3i blockInWorld) {
		return getChunk(Coordinates.convertInWorldToChunk(blockInWorld, null));
	}
	
	public BlockData getBlock(Vec3i blockInWorld) {
		ChunkData chunk = getChunkByBlock(blockInWorld);
		if (chunk == null) return null;
		
		return chunk.getBlock(Coordinates.convertInWorldToInChunk(blockInWorld, null));
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
	
	public Collection<ChunkData> getChunks() {
		return chunks;
	}
	
	public EntityData getEntity(long entityId) {
		return entitiesById.get(entityId);
	}
	
	public Collection<EntityData> getEntities() {
		return entities;
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
