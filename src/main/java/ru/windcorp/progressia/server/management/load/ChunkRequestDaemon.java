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
package ru.windcorp.progressia.server.management.load;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.world.generic.ChunkMap;
import ru.windcorp.progressia.common.world.generic.ChunkMaps;
import ru.windcorp.progressia.common.world.generic.ChunkSet;
import ru.windcorp.progressia.common.world.generic.ChunkSets;
import ru.windcorp.progressia.server.Server;

/**
 * Chunk request daemon gathers chunk requests from players (via {@link VisionManager}) and loads or unloads chunks appropriately.
 */
public class ChunkRequestDaemon {
	
	private static final float CHUNK_UNLOAD_DELAY = Units.get(5, "s");
	
	private final ChunkManager chunkManager;
	
	private final ChunkSet loaded;
	private final ChunkSet requested = ChunkSets.newHashSet();
	private final ChunkSet toLoad = ChunkSets.newHashSet();
	private final ChunkSet toRequestUnload = ChunkSets.newHashSet();
	
	private final Collection<Vec3i> buffer = new ArrayList<>();
	
	private static class ChunkUnloadRequest {
		private final Vec3i chunkPos;
		private final long unloadAt;
		
		public ChunkUnloadRequest(Vec3i chunkPos, long unloadAt) {
			this.chunkPos = chunkPos;
			this.unloadAt = unloadAt;
		}
		
		/**
		 * @return the chunk position
		 */
		public Vec3i getChunkPos() {
			return chunkPos;
		}
		
		/**
		 * @return the moment when the chunks becomes eligible for unloading
		 */
		public long getUnloadAt() {
			return unloadAt;
		}
	}
	
	private final ChunkMap<ChunkUnloadRequest> unloadSchedule = ChunkMaps.newHashMap();

	public ChunkRequestDaemon(ChunkManager chunkManager) {
		this.chunkManager = chunkManager;
		this.loaded = getServer().getWorld().getData().getLoadedChunks();
	}
	
	public void tick() {
		synchronized (getServer().getWorld().getData()) {
			synchronized (getServer().getPlayerManager().getMutex()) {
				loadAndUnloadChunks();
				sendAndRevokeChunks();
			}
		}
	}

	private void loadAndUnloadChunks() {
		gatherLoadRequests();
		updateLoadQueues();
		processLoadQueues();
	}

	private void gatherLoadRequests() {
		requested.clear();

		getChunkManager().getLoadManager().getVisionManager().forEachVision(vision -> {
			vision.getRequestedChunks().clear();
			vision.getPlayer().requestChunksToLoad(vision.getRequestedChunks()::add);
			requested.addAll(vision.getRequestedChunks());
		});
	}

	private void updateLoadQueues() {
		toLoad.clear();
		toLoad.addAll(requested);
		toLoad.removeAll(loaded);

		toRequestUnload.clear();
		toRequestUnload.addAll(loaded);
		toRequestUnload.removeAll(requested);
	}

	private void processLoadQueues() {
		toRequestUnload.forEach(this::scheduleUnload);
		toRequestUnload.clear();
		
		toLoad.forEach(getChunkManager()::loadOrGenerateChunk);
		toLoad.clear();
		
		unloadScheduledChunks();
	}
	
	private void scheduleUnload(Vec3i chunkPos) {
		if (unloadSchedule.containsKey(chunkPos)) {
			// Unload already requested, skip
			return;
		}

		long unloadAt = System.currentTimeMillis() + (long) (getUnloadDelay() * 1000);
		Vec3i chunkPosCopy = new Vec3i(chunkPos);
		
		unloadSchedule.put(chunkPosCopy, new ChunkUnloadRequest(chunkPosCopy, unloadAt));
	}
	
	private void unloadScheduledChunks() {
		long now = System.currentTimeMillis();
		
		for (Iterator<ChunkUnloadRequest> it = unloadSchedule.values().iterator(); it.hasNext();) {
			ChunkUnloadRequest request = it.next();
			
			if (request.getUnloadAt() < now) {
				it.remove();
				getChunkManager().unloadChunk(request.getChunkPos());
			}
		}
	}

	private void sendAndRevokeChunks() {
		getChunkManager().getLoadManager().getVisionManager().forEachVision(vision -> {
			revokeChunks(vision);
			sendChunks(vision);
		});
	}

	private void sendChunks(PlayerVision vision) {
		vision.getRequestedChunks().forEachIn(getServer().getWorld(), chunk -> {
			if (!chunk.isReady())
				return;
			if (vision.isChunkVisible(chunk.getPosition()))
				return;
			buffer.add(chunk.getPosition());
		});
		
		if (buffer.isEmpty()) return;
		for (Vec3i chunkPos : buffer) {
			getChunkManager().sendChunk(vision.getPlayer(), chunkPos);
		}
		
		buffer.clear();
	}
	
	private void revokeChunks(PlayerVision vision) {
		vision.getVisibleChunks().forEach(chunkPos -> {
			if (getChunkManager().isChunkLoaded(chunkPos) && vision.getRequestedChunks().contains(chunkPos))
				return;
			buffer.add(new Vec3i(chunkPos));
		});
		
		if (buffer.isEmpty()) return;
		for (Vec3i chunkPos : buffer) {
			getChunkManager().revokeChunk(vision.getPlayer(), chunkPos);
		}
		
		buffer.clear();
	}

	/**
	 * @return the minimum amount of time a chunk will spend in the unload queue
	 */
	public float getUnloadDelay() {
		return CHUNK_UNLOAD_DELAY;
	}
	
	/**
	 * @return the manager
	 */
	public ChunkManager getChunkManager() {
		return chunkManager;
	}
	
	public Server getServer() {
		return getChunkManager().getServer();
	}

}
