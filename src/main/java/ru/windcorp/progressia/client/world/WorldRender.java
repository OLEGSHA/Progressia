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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.WeakHashMap;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.graphics.backend.FaceCulling;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.world.block.BlockRender;
import ru.windcorp.progressia.client.world.entity.EntityRenderRegistry;
import ru.windcorp.progressia.client.world.entity.EntityRenderable;
import ru.windcorp.progressia.client.world.tile.TileRender;
import ru.windcorp.progressia.client.world.tile.TileRenderStack;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.ChunkDataListeners;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.WorldDataListener;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.ChunkSet;
import ru.windcorp.progressia.common.world.generic.ChunkSets;
import ru.windcorp.progressia.common.world.generic.GenericWorld;

public class WorldRender
		implements GenericWorld<BlockRender, TileRender, TileRenderStack, ChunkRender, EntityRenderable> {

	private final WorldData data;
	private final Client client;

	private final Map<ChunkData, ChunkRender> chunks = Collections.synchronizedMap(new HashMap<>());
	private final Map<EntityData, EntityRenderable> entityModels = Collections.synchronizedMap(new WeakHashMap<>());

	private final ChunkSet chunksToUpdate = ChunkSets.newSyncHashSet();

	public WorldRender(WorldData data, Client client) {
		this.data = data;
		this.client = client;

		data.addListener(ChunkDataListeners.createAdder(new ChunkUpdateListener(this)));
		data.addListener(new WorldDataListener() {
			@Override
			public void onChunkLoaded(WorldData world, ChunkData chunk) {
				addChunk(chunk);
			}

			@Override
			public void beforeChunkUnloaded(WorldData world, ChunkData chunk) {
				removeChunk(chunk);
			}
		});
	}

	protected void addChunk(ChunkData chunk) {
		chunks.put(chunk, new ChunkRender(WorldRender.this, chunk));
		markChunkForUpdate(chunk.getPosition());
	}

	protected void removeChunk(ChunkData chunk) {
		chunks.remove(chunk);
	}

	public WorldData getData() {
		return data;
	}

	public Client getClient() {
		return client;
	}

	public ChunkRender getChunk(ChunkData chunkData) {
		return chunks.get(chunkData);
	}

	@Override
	public ChunkRender getChunk(Vec3i pos) {
		return chunks.get(getData().getChunk(pos));
	}

	@Override
	public Collection<ChunkRender> getChunks() {
		return chunks.values();
	}

	@Override
	public Collection<EntityRenderable> getEntities() {
		return entityModels.values();
	}

	public void render(ShapeRenderHelper renderer) {
		updateChunks();

		getChunks().forEach(chunk -> chunk.render(renderer));
		renderEntities(renderer);
	}

	private void updateChunks() {
		synchronized (chunksToUpdate) {
			if (chunksToUpdate.isEmpty())
				return;

			int updates = updateChunksNearLocalPlayer();
			if (updates > 0 || chunksToUpdate.isEmpty())
				return;

			updateRandomChunk();
		}
	}

	private int updateChunksNearLocalPlayer() {
		EntityData entity = getClient().getLocalPlayer().getEntity();
		if (entity == null)
			return 0;

		int[] updates = new int[] { 0 };

		VectorUtil.iterateCuboidAround(entity.getChunkCoords(null), 3, chunkPos -> {
			if (!chunksToUpdate.contains(chunkPos))
				return;

			ChunkRender chunk = getChunk(chunkPos);
			if (chunk == null)
				return;

			chunk.update();
			chunksToUpdate.remove(chunkPos);
			updates[0]++;
		});

		return updates[0];
	}

	private void updateRandomChunk() {
		EntityData entity = getClient().getLocalPlayer().getEntity();

		Vec3 playerPos = entity == null ? Vectors.ZERO_3 : entity.getPosition();

		ChunkRender nearest = null;
		float nearestDistSq = Float.POSITIVE_INFINITY;

		Vec3 v = Vectors.grab3();

		for (Iterator<Vec3i> it = chunksToUpdate.iterator(); it.hasNext();) {
			Vec3i chunkPos = it.next();
			ChunkRender chunk = getChunk(chunkPos);

			if (chunk == null) {
				it.remove();
				continue;
			}

			v.set(chunk.getMinX(), chunk.getMinY(), chunk.getMinZ()).sub(playerPos);
			float distSq = v.x * v.x + v.y * v.y + v.z * v.z;

			if (nearest == null || distSq < nearestDistSq) {
				nearest = chunk;
				nearestDistSq = distSq;
			}
		}

		if (nearest != null) {
			nearest.update();
			chunksToUpdate.remove(nearest.getPosition());
		}

		Vectors.release(v);
	}

	public int getPendingChunkUpdates() {
		return chunksToUpdate.size();
	}

	private void renderEntities(ShapeRenderHelper renderer) {
		FaceCulling.push(false);

		getData().forEachEntity(entity -> {
			renderer.pushTransform().translate(entity.getPosition());
			getEntityRenderable(entity).render(renderer);
			renderer.popTransform();
		});

		FaceCulling.pop();
	}

	public EntityRenderable getEntityRenderable(EntityData entity) {
		return entityModels.computeIfAbsent(entity, WorldRender::createEntityRenderable);
	}

	private static EntityRenderable createEntityRenderable(EntityData entity) {
		return EntityRenderRegistry.getInstance().get(entity.getId()).createRenderable(entity);
	}

	public void markChunkForUpdate(Vec3i chunkPos) {
		if (getData().getChunk(chunkPos) != null) {
			chunksToUpdate.add(chunkPos);
		}
	}

}
