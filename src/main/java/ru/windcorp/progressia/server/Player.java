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

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.PlayerData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.comms.ClientPlayer;

public class Player extends PlayerData implements ChunkLoader {

	private final Server server;
	private final ClientPlayer client;

	public Player(EntityData entity, Server server, ClientPlayer client) {
		super(entity);
		this.server = server;
		this.client = client;

		client.setPlayer(this);
	}

	public Server getServer() {
		return server;
	}

	public ClientPlayer getClient() {
		return client;
	}

	@Override
	public void requestChunksToLoad(Consumer<Vec3i> chunkConsumer) {
		Vec3i start = getEntity().getPosition().round_();
		Coordinates.convertInWorldToChunk(start, start);

		Vec3i cursor = new Vec3i();
		float radius = getServer().getLoadDistance(this) / Units.get(ChunkData.BLOCKS_PER_CHUNK, "m");

		float radiusSq = radius * radius;
		int iRadius = (int) Math.ceil(radius);

		for (cursor.x = -iRadius; cursor.x <= +iRadius; ++cursor.x) {
			for (cursor.y = -iRadius; cursor.y <= +iRadius; ++cursor.y) {
				for (cursor.z = -iRadius; cursor.z <= +iRadius; ++cursor.z) {
					if (cursor.x * cursor.x + cursor.y * cursor.y + (cursor.z * 2) * (cursor.z * 2) <= radiusSq) {

						cursor.add(start);
						chunkConsumer.accept(cursor);
						cursor.sub(start);

					}
				}
			}
		}
	}

	public String getLogin() {
		return getClient().getLogin();
	}

}
