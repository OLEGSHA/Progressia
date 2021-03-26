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
import gnu.trove.TCollections;
import gnu.trove.set.TLongSet;
import gnu.trove.set.hash.TLongHashSet;
import ru.windcorp.progressia.common.world.generic.ChunkSet;
import ru.windcorp.progressia.common.world.generic.ChunkSets;
import ru.windcorp.progressia.server.Player;

public class PlayerVision {
	
	private final Player player;

	private final ChunkSet visibleChunks = ChunkSets.newSyncHashSet();
	private final ChunkSet requestedChunks = ChunkSets.newHashSet();
	
	private final TLongSet visibleEntities = TCollections.synchronizedSet(new TLongHashSet());
	private final TLongSet requestedEntities = new TLongHashSet();

	public PlayerVision(Player player) {
		this.player = player;
	}

	public boolean isChunkVisible(Vec3i chunkPos) {
		return visibleChunks.contains(chunkPos);
	}
	
	public boolean isEntityVisible(long entityId) {
		return visibleEntities.contains(entityId);
	}
	
	/**
	 * @return the requestedChunks
	 */
	public ChunkSet getRequestedChunks() {
		return requestedChunks;
	}
	
	/**
	 * @return the visibleChunks
	 */
	public ChunkSet getVisibleChunks() {
		return visibleChunks;
	}
	
	/**
	 * @return the requestedEntities
	 */
	public TLongSet getRequestedEntities() {
		return requestedEntities;
	}
	
	/**
	 * @return the visibleEntities
	 */
	public TLongSet getVisibleEntities() {
		return visibleEntities;
	}
	
	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

}
