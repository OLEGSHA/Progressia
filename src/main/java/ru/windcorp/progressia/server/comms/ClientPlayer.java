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

package ru.windcorp.progressia.server.comms;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.generic.ChunkSet;
import ru.windcorp.progressia.common.world.generic.ChunkSets;
import ru.windcorp.progressia.server.Player;

public abstract class ClientPlayer extends ClientChat {

	private Player player;

	public ClientPlayer(int id) {
		super(id);
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public abstract String getLogin();

	public boolean isChunkVisible(Vec3i chunkPos) {
		if (player == null)
			return false;
		return player.getServer().getChunkManager().isChunkVisible(chunkPos, player);
	}

	public ChunkSet getVisibleChunks() {
		if (player == null)
			return ChunkSets.empty();
		return player.getServer().getChunkManager().getVisibleChunks(player);
	}

	public boolean isChunkVisible(long entityId) {
		return true;
	}

}
