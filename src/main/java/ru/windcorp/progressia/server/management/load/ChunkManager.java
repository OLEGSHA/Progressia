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

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.PacketRevokeChunk;
import ru.windcorp.progressia.common.world.PacketSendChunk;
import ru.windcorp.progressia.common.world.DefaultWorldData;
import ru.windcorp.progressia.server.Player;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.test.TestWorldDiskIO;

/**
 * Chunk manager provides facilities to load, unload and generate chunks for a
 * {@link Server} on demand.
 */
public class ChunkManager {

	private final LoadManager loadManager;

	public ChunkManager(LoadManager loadManager) {
		this.loadManager = loadManager;
	}

	/**
	 * @return the loadManager
	 */
	public LoadManager getLoadManager() {
		return loadManager;
	}

	/**
	 * @return the server
	 */
	public Server getServer() {
		return getLoadManager().getServer();
	}

	/**
	 * Describes the result of an attempt to load a chunk.
	 */
	public static enum LoadResult {
		/**
		 * A chunk has successfully been read from disk and is now loaded.
		 */
		LOADED_FROM_DISK,

		/**
		 * A chunk has successfully been generated and is now loaded.
		 */
		GENERATED,

		/**
		 * A chunk has already been loaded and so no action has been taken.
		 */
		ALREADY_LOADED,

		/**
		 * A chunk has not been loaded previously and the operation has failed
		 * to load it. It is not currently loaded.
		 */
		NOT_LOADED
	}

	/**
	 * Loads or generates the chunk at the given location unless it is already
	 * loaded. The chunk is loaded after this method completes normally.
	 * 
	 * @param chunkPos the position of the chunk
	 * @return one of {@link LoadResult#LOADED_FROM_DISK LOADED_FROM_DISK},
	 *         {@link LoadResult#GENERATED GENERATED} or
	 *         {@link LoadResult#ALREADY_LOADED ALREADY_LOADED}
	 */
	public LoadResult loadOrGenerateChunk(Vec3i chunkPos) {
		LoadResult loadResult = loadChunk(chunkPos);

		if (loadResult == LoadResult.NOT_LOADED || !getServer().getWorld().getChunk(chunkPos).isReady()) {
			getServer().getWorld().generate(chunkPos);
			return LoadResult.GENERATED;
		} else {
			return loadResult;
		}
	}

	/**
	 * Attempts to load the chunk from disk unless it is already loaded. If the
	 * chunk is not currently loaded and it is not available on the disk this
	 * method does nothing.
	 * 
	 * @param chunkPos the position of the chunk
	 * @return one of {@link LoadResult#LOADED_FROM_DISK LOADED_FROM_DISK},
	 *         {@link LoadResult#NOT_LOADED NOT_LOADED} or
	 *         {@link LoadResult#ALREADY_LOADED ALREADY_LOADED}
	 */
	public LoadResult loadChunk(Vec3i chunkPos) {
		if (isChunkLoaded(chunkPos)) {
			return LoadResult.ALREADY_LOADED;
		}

		DefaultWorldData world = getServer().getWorld().getData();

		DefaultChunkData chunk = TestWorldDiskIO.tryToLoad(chunkPos, world, getServer());
		if (chunk != null) {
			world.addChunk(chunk);
			return LoadResult.LOADED_FROM_DISK;
		} else {
			return LoadResult.NOT_LOADED;
		}
	}

	/**
	 * Unloads the chunk and saves it to disk if the chunk is loaded, otherwise
	 * does nothing.
	 * 
	 * @param chunkPos the position of the chunk
	 * @return {@code true} iff the chunk had been loaded and was unloaded by
	 *         this method
	 */
	public boolean unloadChunk(Vec3i chunkPos) {
		DefaultWorldData world = getServer().getWorld().getData();
		DefaultChunkData chunk = world.getChunk(chunkPos);

		if (chunk == null) {
			return false;
		}

		world.removeChunk(chunk);
		TestWorldDiskIO.saveChunk(chunk, getServer());

		return true;
	}

	public void sendChunk(Player player, Vec3i chunkPos) {
		DefaultChunkData chunk = getServer().getWorld().getData().getChunk(chunkPos);

		if (chunk == null) {
			throw new IllegalStateException(
				String.format(
					"Chunk (%d; %d; %d) is not loaded, cannot send",
					chunkPos.x,
					chunkPos.y,
					chunkPos.z
				)
			);
		}

		PacketSendChunk packet = new PacketSendChunk();
		packet.set(chunk);
		player.getClient().sendPacket(packet);

		getLoadManager().getVisionManager().getVision(player, true).getVisibleChunks().add(chunkPos);
	}

	public void revokeChunk(Player player, Vec3i chunkPos) {
		PacketRevokeChunk packet = new PacketRevokeChunk();
		packet.set(chunkPos);
		player.getClient().sendPacket(packet);

		PlayerVision vision = getLoadManager().getVisionManager().getVision(player, false);
		if (vision != null) {
			vision.getVisibleChunks().remove(chunkPos);
		}
	}

	/**
	 * Checks whether or not the chunk at the specified location is loaded. A
	 * loaded chunk is accessible through the server's {@link DefaultWorldData} object.
	 * 
	 * @param chunkPos the position of the chunk
	 * @return {@code true} iff the chunk is loaded
	 */
	public boolean isChunkLoaded(Vec3i chunkPos) {
		return getServer().getWorld().isChunkLoaded(chunkPos);
	}

}
