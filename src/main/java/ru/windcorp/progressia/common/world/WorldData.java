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

import java.util.Collection;
import java.util.Collections;

import glm.vec._3.i.Vec3i;
import gnu.trove.impl.sync.TSynchronizedLongObjectMap;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import ru.windcorp.progressia.common.util.CoordinatePacker;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class WorldData {

	private final TLongObjectMap<ChunkData> chunksByPos =
			new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>(), this);
	
	private final Collection<ChunkData> chunks =
			Collections.unmodifiableCollection(chunksByPos.valueCollection());
	
	private final TLongObjectMap<EntityData> entitiesById =
			new TSynchronizedLongObjectMap<>(new TLongObjectHashMap<>(), this);
	
	private final Collection<EntityData> entities =
			Collections.unmodifiableCollection(entitiesById.valueCollection());
	
	public WorldData() {
		final int size = 1;
		
		for (int x = -(size / 2); x <= (size / 2); ++x) {
			for (int y = -(size / 2); y <= (size / 2); ++y) {
				addChunk(new ChunkData(x, y, 0, this));
			}
		}
	}
	
	private synchronized void addChunk(ChunkData chunk) {
		chunksByPos.put(getChunkKey(chunk), chunk);
		
		chunk.forEachEntity(entity ->
			entitiesById.put(entity.getEntityId(), entity)
		);
	}
	
//	private synchronized void removeChunk(ChunkData chunk) {
//		chunksByPos.remove(getChunkKey(chunk));
//		
//		chunk.forEachEntity(entity ->
//			entitiesById.remove(entity.getEntityId())
//		);
//	}
	
	private static long getChunkKey(ChunkData chunk) {
		return CoordinatePacker.pack3IntsIntoLong(chunk.getPosition());
	}
	
	public ChunkData getChunk(Vec3i pos) {
		return chunksByPos.get(CoordinatePacker.pack3IntsIntoLong(pos));
	}
	
	public ChunkData getChunkByBlock(Vec3i blockInWorld) {
		Vec3i chunkPos = Vectors.grab3i();
		Coordinates.convertInWorldToChunk(blockInWorld, chunkPos);
		ChunkData result = getChunk(chunkPos);
		Vectors.release(chunkPos);
		return result;
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
	
}
