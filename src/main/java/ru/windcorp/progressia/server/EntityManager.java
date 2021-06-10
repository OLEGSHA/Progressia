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
import gnu.trove.TCollections;
import gnu.trove.iterator.TLongIterator;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import ru.windcorp.jputil.chars.StringUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.entity.PacketRevokeEntity;
import ru.windcorp.progressia.common.world.entity.PacketSendEntity;
import ru.windcorp.progressia.common.world.generic.ChunkSet;

public class EntityManager {

	private class PlayerVision {

		private final TLongSet visible = TCollections.synchronizedSet(new TLongHashSet());
		private final TLongSet requested = new TLongHashSet();
		private final TLongSet toSend = new TLongHashSet();
		private final TLongSet toRevoke = new TLongHashSet();

		public boolean isEntityVisible(long entityId) {
			return visible.contains(entityId);
		}

		public void gatherRequests(Player player) {
			requested.clear();

			ChunkSet visibleChunks = player.getClient().getVisibleChunks();
			Vec3i v = Vectors.grab3i();

			getServer().getWorld().forEachEntity(entity -> {
				if (visibleChunks.contains(entity.getChunkCoords(v))) {
					requested.add(entity.getEntityId());
				}
			});

			Vectors.release(v);
		}

		public void updateQueues(Player player) {
			toSend.clear();
			toSend.addAll(requested);
			toSend.removeAll(visible);
			toSend.retainAll(loaded);

			toRevoke.clear();

			for (TLongIterator it = visible.iterator(); it.hasNext();) {
				long entityId = it.next();
				if (!loaded.contains(entityId) || !requested.contains(entityId)) {
					toRevoke.add(entityId);
				}
			}
		}

		public void processQueues(Player player) {
			toRevoke.forEach(entityId -> {
				revokeEntity(player, entityId);
				return true;
			});
			toRevoke.clear();

			toSend.forEach(entityId -> {
				sendEntity(player, entityId);
				return true;
			});
			toSend.clear();
		}

	}

	private final Server server;

	private final TLongSet loaded;

	// TODO replace with a normal Map managed by some sort of PlayerListener,
	// weak maps are weak
	private final Map<Player, PlayerVision> visions = Collections.synchronizedMap(new WeakHashMap<>());

	public EntityManager(Server server) {
		this.server = server;
		this.loaded = server.getWorld().getData().getLoadedEntities();
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
		server.getPlayerManager().getPlayers().forEach(p -> {
			PlayerVision vision = getVision(p, true);
			vision.gatherRequests(p);
		});
	}

	private void updateQueues() {
		visions.forEach((p, v) -> {
			v.updateQueues(p);
		});
	}

	private void processQueues() {
		visions.forEach((p, v) -> {
			v.processQueues(p);
		});
	}

	private PlayerVision getVision(Player player, boolean createIfMissing) {
		return createIfMissing ? visions.computeIfAbsent(player, k -> new PlayerVision()) : visions.get(player);
	}

	public void sendEntity(Player player, long entityId) {

		EntityData entity = server.getWorld().getData().getEntity(entityId);

		if (entity == null) {
			throw new IllegalStateException("Entity with entity ID " + new String(StringUtil.toFullHex(entityId))
					+ " is not loaded, cannot send");
		}

		PacketSendEntity packet = new PacketSendEntity();
		packet.set(entity);
		player.getClient().sendPacket(packet);

		getVision(player, true).visible.add(entityId);
	}

	public void revokeEntity(Player player, long entityId) {
		PacketRevokeEntity packet = new PacketRevokeEntity();
		packet.set(entityId);
		player.getClient().sendPacket(packet);

		PlayerVision vision = getVision(player, false);
		if (vision != null) {
			vision.visible.remove(entityId);
		}
	}

	public boolean isEntityVisible(long entityId, Player player) {
		PlayerVision vision = getVision(player, false);

		if (vision == null) {
			return false;
		}

		return vision.isEntityVisible(entityId);
	}

	private static final TLongSet EMPTY_LONG_SET = TCollections.unmodifiableSet(new TLongHashSet());

	public TLongSet getVisibleEntities(Player player) {
		PlayerVision vision = getVision(player, false);

		if (vision == null) {
			return EMPTY_LONG_SET;
		}

		return vision.visible;
	}

	public Server getServer() {
		return server;
	}

}
