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

import gnu.trove.set.TLongSet;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.entity.PacketRevokeEntity;
import ru.windcorp.progressia.common.world.entity.PacketSendEntity;
import ru.windcorp.progressia.server.Player;
import ru.windcorp.progressia.server.Server;

public class EntityManager {

	private final LoadManager loadManager;

	private final TLongSet loaded;

	public EntityManager(LoadManager loadManager) {
		this.loadManager = loadManager;
		this.loaded = getServer().getWorld().getData().getLoadedEntities();
	}

	public void sendEntity(Player player, long entityId) {
		PlayerVision vision = getLoadManager().getVisionManager().getVision(player, true);
		if (!vision.getVisibleEntities().add(entityId)) {
			return;
		}
		
		EntityData entity = getServer().getWorld().getData().getEntity(entityId);

		if (entity == null) {
			throw new IllegalStateException(
				"Entity with entity ID " + EntityData.formatEntityId(entityId) + " is not loaded, cannot send"
			);
		}

		PacketSendEntity packet = new PacketSendEntity();
		packet.set(entity);
		player.getClient().sendPacket(packet);
	}

	public void revokeEntity(Player player, long entityId) {
		PlayerVision vision = getLoadManager().getVisionManager().getVision(player, false);
		if (vision == null) {
			return;
		}
		if (!vision.getVisibleEntities().remove(entityId)) {
			return;
		}
		
		PacketRevokeEntity packet = new PacketRevokeEntity();
		packet.set(entityId);
		player.getClient().sendPacket(packet);
	}

	public boolean isEntityVisible(Player player, long entityId) {
		PlayerVision vision = getLoadManager().getVisionManager().getVision(player, false);

		if (vision == null) {
			return false;
		}

		return vision.isEntityVisible(entityId);
	}
	
	public boolean isEntityLoaded(long entityId) {
		return loaded.contains(entityId);
	}
	
	/**
	 * @return the loadManager
	 */
	public LoadManager getLoadManager() {
		return loadManager;
	}

	public Server getServer() {
		return getLoadManager().getServer();
	}

}
