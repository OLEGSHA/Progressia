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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

import com.google.common.eventbus.Subscribe;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.generic.ChunkSet;
import ru.windcorp.progressia.common.world.generic.ChunkSets;
import ru.windcorp.progressia.server.Player;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.events.PlayerJoinedEvent;
import ru.windcorp.progressia.server.events.PlayerLeftEvent;

public class VisionManager {
	
	private final LoadManager loadManager;
	
	private final Map<Player, PlayerVision> visions = Collections.synchronizedMap(new HashMap<>());

	public VisionManager(LoadManager loadManager) {
		this.loadManager = loadManager;
		getServer().subscribe(this);
	}
	
	@Subscribe
	private void onPlayerJoined(PlayerJoinedEvent event) {
		System.out.println("VisionManager.onPlayerJoined()");
		getVision(event.getPlayer(), true);
	}
	
	@Subscribe
	private void onPlayerLeft(PlayerLeftEvent event) {
		System.out.println("VisionManager.onPlayerLeft()");
		visions.remove(event.getPlayer());
	}

	public PlayerVision getVision(Player player, boolean createIfMissing) {
		if (createIfMissing) {
			return visions.computeIfAbsent(player, PlayerVision::new);
		} else {
			return visions.get(player);
		}
	}
	
	public void forEachVision(Consumer<? super PlayerVision> action) {
		visions.values().forEach(action);
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

		return vision.getVisibleChunks();
	}
	
	/**
	 * @return the loadManager
	 */
	public LoadManager getLoadManager() {
		return loadManager;
	}
	
	/**
	 * @return the chunkManager
	 */
	public ChunkManager getChunkManager() {
		return getLoadManager().getChunkManager();
	}
	
	public Server getServer() {
		return getLoadManager().getServer();
	}

}
