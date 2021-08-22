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

package ru.windcorp.progressia.common.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import gnu.trove.TCollections;
import gnu.trove.map.TLongObjectMap;
import gnu.trove.map.hash.TLongObjectHashMap;
import gnu.trove.set.TLongSet;
import ru.windcorp.progressia.common.collision.CollisionModel;
import ru.windcorp.progressia.common.state.StateChange;
import ru.windcorp.progressia.common.state.StatefulObject;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.ChunkMap;
import ru.windcorp.progressia.common.world.generic.ChunkSet;
import ru.windcorp.progressia.common.world.generic.EntityGeneric;
import ru.windcorp.progressia.common.world.rels.BlockFace;
import ru.windcorp.progressia.common.world.generic.LongBasedChunkMap;

public class DefaultWorldData implements WorldData {

	private final ChunkMap<DefaultChunkData> chunksByPos = new LongBasedChunkMap<>(
		TCollections.synchronizedMap(new TLongObjectHashMap<>())
	);

	private final Collection<DefaultChunkData> chunks = Collections.unmodifiableCollection(chunksByPos.values());

	private final TLongObjectMap<EntityData> entitiesById = TCollections.synchronizedMap(new TLongObjectHashMap<>());

	private final Collection<EntityData> entities = Collections.unmodifiableCollection(entitiesById.valueCollection());

	private GravityModel gravityModel = null;

	private float time = 0;

	private final Collection<WorldDataListener> listeners = Collections.synchronizedCollection(new ArrayList<>());

	public DefaultWorldData() {

	}

	@Override
	public DefaultChunkData getChunk(Vec3i pos) {
		return chunksByPos.get(pos);
	}
	
	@Override
	public DefaultChunkData getChunkByBlock(Vec3i blockInWorld) {
		return (DefaultChunkData) WorldData.super.getChunkByBlock(blockInWorld);
	}

	@Override
	public Collection<DefaultChunkData> getChunks() {
		return chunks;
	}

	public ChunkSet getLoadedChunks() {
		return chunksByPos.keys();
	}

	@Override
	public Collection<EntityData> getEntities() {
		return entities;
	}

	@Override
	public void forEachEntity(Consumer<? super EntityData> action) {
		synchronized (entitiesById) { // TODO HORRIBLY MUTILATE THE CORPSE OF
										// TROVE4J so that
										// gnu.trove.impl.sync.SynchronizedCollection.forEach
										// is synchronized
			getEntities().forEach(action);
		}
	}

	public TLongSet getLoadedEntities() {
		return entitiesById.keySet();
	}

	private void addChunkListeners(DefaultChunkData chunk) {
		getListeners().forEach(l -> l.getChunkListeners(this, chunk.getPosition(), chunk::addListener));
	}

	public synchronized void addChunk(DefaultChunkData chunk) {
		addChunkListeners(chunk);

		DefaultChunkData previous = chunksByPos.get(chunk);
		if (previous != null) {
			throw new IllegalArgumentException(
				String.format(
					"Chunk at (%d; %d; %d) already exists",
					chunk.getPosition().x,
					chunk.getPosition().y,
					chunk.getPosition().z
				)
			);
		}

		chunksByPos.put(chunk, chunk);

		chunk.onLoaded();
		getListeners().forEach(l -> l.onChunkLoaded(this, chunk));
	}

	public synchronized void removeChunk(DefaultChunkData chunk) {
		getListeners().forEach(l -> l.beforeChunkUnloaded(this, chunk));
		chunk.beforeUnloaded();

		chunksByPos.remove(chunk);
	}

	@Override
	public void setBlock(Vec3i blockInWorld, BlockData block, boolean notify) {
		DefaultChunkData chunk = getChunkByBlock(blockInWorld);
		if (chunk == null)
			throw new IllegalCoordinatesException(
				"Coordinates "
					+ "(" + blockInWorld.x + "; " + blockInWorld.y + "; " + blockInWorld.z + ") "
					+ "do not belong to a loaded chunk"
			);

		chunk.setBlock(Coordinates.convertInWorldToInChunk(blockInWorld, null), block, notify);
	}

	@Override
	public TileDataStack getTiles(Vec3i blockInWorld, BlockFace face) {
		return WorldData.super.getTiles(blockInWorld, face);
	}

	@Override
	public EntityData getEntity(long entityId) {
		return entitiesById.get(entityId);
	}

	@Override
	public void addEntity(EntityData entity) {
		Objects.requireNonNull(entity, "entity");

		EntityData previous = entitiesById.putIfAbsent(entity.getEntityId(), entity);

		if (previous != null) {
			String message = "Cannot add entity " + entity + ": ";

			if (previous == entity) {
				message += "already present";
			} else {
				message += "entity with the same EntityID already present (" + previous + ")";
			}

			throw new IllegalStateException(message);
		}

		getListeners().forEach(l -> l.onEntityAdded(this, entity));
	}

	@Override
	public void removeEntity(long entityId) {
		synchronized (entitiesById) {
			EntityData entity = entitiesById.get(entityId);

			if (entity == null) {
				throw new IllegalArgumentException(
					"Entity with EntityID " + EntityData.formatEntityId(entityId) + " not present"
				);
			} else {
				removeEntity(entity);
			}
		}
	}

	@Override
	public void removeEntity(EntityData entity) {
		Objects.requireNonNull(entity, "entity");

		getListeners().forEach(l -> l.beforeEntityRemoved(this, entity));
		entitiesById.remove(entity.getEntityId());
	}

	@Override
	public <SE extends StatefulObject & EntityGeneric> void changeEntity(SE entity, StateChange<SE> change) {
		change.change(entity);
	}

	@Override
	public float getTime() {
		return time;
	}

	@Override
	public void advanceTime(float change) {
		this.time += change;
	}

	public CollisionModel getCollisionModelOfBlock(Vec3i blockInWorld) {
		DefaultChunkData chunk = getChunkByBlock(blockInWorld);
		if (chunk == null)
			return null;

		BlockData block = chunk.getBlock(Coordinates.convertInWorldToInChunk(blockInWorld, null));
		if (block == null)
			return null;
		return block.getCollisionModel();
	}

	/**
	 * @return the gravity model
	 */
	@Override
	public GravityModel getGravityModel() {
		return gravityModel;
	}

	/**
	 * @param gravityModel the gravity model to set
	 */
	public void setGravityModel(GravityModel gravityModel) {
		if (!chunks.isEmpty()) {
			throw new IllegalStateException(
				"Attempted to change gravity model to " + gravityModel + " while " + chunks.size()
					+ " chunks were loaded"
			);
		}

		this.gravityModel = gravityModel;
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
