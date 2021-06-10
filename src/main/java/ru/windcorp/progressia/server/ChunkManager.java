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

package ru.windcorp.progressia.server;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.PacketRevokeChunk;
import ru.windcorp.progressia.common.world.PacketSendChunk;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.generic.ChunkSet;
import ru.windcorp.progressia.common.world.generic.ChunkSets;
import ru.windcorp.progressia.test.TestWorldDiskIO;

public class ChunkManager {

	private class PlayerVision {

		private final ChunkSet visible = ChunkSets.newSyncHashSet();
		private final ChunkSet requested = ChunkSets.newHashSet();
		private final ChunkSet toSend = ChunkSets.newHashSet();
		private final ChunkSet toRevoke = ChunkSets.newHashSet();

		public boolean isChunkVisible(Vec3i chunkPos) {
			return visible.contains(chunkPos);
		}

		public void gatherRequests(Player player) {
			requested.clear();
			player.requestChunksToLoad(requested::add);
		}

		public void updateQueues(Player player) {
			toSend.clear();

			requested.forEachIn(server.getWorld(), chunk -> {
				if (!chunk.isReady())
					return;
				if (visible.contains(chunk))
					return;
				toSend.add(chunk);
			});

			toRevoke.clear();
			toRevoke.addAll(visible);
			toRevoke.removeIf(v -> loaded.contains(v) && requested.contains(v));
		}

		public void processQueues(Player player) {
			toRevoke.forEach(chunkPos -> revokeChunk(player, chunkPos));
			toRevoke.clear();

			toSend.forEach(chunkPos -> sendChunk(player, chunkPos));
			toSend.clear();
		}

	}

	private final Server server;

	private final ChunkSet loaded;
	private final ChunkSet requested = ChunkSets.newHashSet();
	private final ChunkSet toLoad = ChunkSets.newHashSet();
	private final ChunkSet toUnload = ChunkSets.newHashSet();

	// TODO replace with a normal Map managed by some sort of PlayerListener,
	// weak maps are weak
	private final Map<Player, PlayerVision> visions = Collections.synchronizedMap(new WeakHashMap<>());

	public ChunkManager(Server server) {
		this.server = server;
		this.loaded = server.getWorld().getData().getLoadedChunks();
	}

	public void tick() {
		synchronized (getServer().getWorld().getData()) {
			synchronized (visions) {
				gatherRequests();
				updateQueues();
				processQueues();
			}
		}
	}

	private void gatherRequests() {
		requested.clear();

		server.getPlayerManager().getPlayers().forEach(p -> {
			PlayerVision vision = getVision(p, true);
			vision.gatherRequests(p);
			requested.addAll(vision.requested);
		});
	}

	private void updateQueues() {
		toLoad.clear();
		toLoad.addAll(requested);
		toLoad.removeAll(loaded);

		toUnload.clear();
		toUnload.addAll(loaded);
		toUnload.removeAll(requested);

		visions.forEach((p, v) -> {
			v.updateQueues(p);
		});
	}

	private void processQueues() {
		toUnload.forEach(this::unloadChunk);
		toUnload.clear();
		toLoad.forEach(this::loadChunk);
		toLoad.clear();

		visions.forEach((p, v) -> {
			v.processQueues(p);
		});
	}

	private PlayerVision getVision(Player player, boolean createIfMissing) {
		return createIfMissing ? visions.computeIfAbsent(player, k -> new PlayerVision()) : visions.get(player);
	}

	public void loadChunk(Vec3i chunkPos) {

		WorldData world = getServer().getWorld().getData();

		ChunkData chunk = TestWorldDiskIO.tryToLoad(chunkPos, world, getServer());
		if (chunk != null) {
			world.addChunk(chunk);
		} else {
			getServer().getWorld().generate(chunkPos);
		}

	}

	public void unloadChunk(Vec3i chunkPos) {

		WorldData world = getServer().getWorld().getData();

		ChunkData chunk = world.getChunk(chunkPos);
		if (chunk == null) {
			throw new IllegalStateException(
					String.format("Chunk (%d; %d; %d) not loaded, cannot unload", chunkPos.x, chunkPos.y, chunkPos.z));
		}

		world.removeChunk(chunk);

		TestWorldDiskIO.saveChunk(chunk, getServer());

	}

	public void sendChunk(Player player, Vec3i chunkPos) {
		ChunkData chunk = server.getWorld().getData().getChunk(chunkPos);

		if (chunk == null) {
			throw new IllegalStateException(
					String.format("Chunk (%d; %d; %d) is not loaded, cannot send", chunkPos.x, chunkPos.y, chunkPos.z));
		}

		PacketSendChunk packet = new PacketSendChunk();
		packet.set(chunk);
		player.getClient().sendPacket(packet);

		getVision(player, true).visible.add(chunkPos);
	}

	public void revokeChunk(Player player, Vec3i chunkPos) {
		PacketRevokeChunk packet = new PacketRevokeChunk();
		packet.set(chunkPos);
		player.getClient().sendPacket(packet);

		PlayerVision vision = getVision(player, false);
		if (vision != null) {
			vision.visible.remove(chunkPos);
		}
	}

	public boolean isChunkVisible(Vec3i chunkPos, Player player) {
		PlayerVision vision = getVision(player, false);

		if (vision == null) {
			return false;
		}

		return vision.isChunkVisible(chunkPos);
	}

	public ChunkSet getVisibleChunks(Player player) {
		PlayerVision vision = getVision(player, false);

		if (vision == null) {
			return ChunkSets.empty();
		}

		return vision.visible;
	}

	public Server getServer() {
		return server;
	}

}
