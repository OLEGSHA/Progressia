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

import glm.mat._3.Mat3;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.Units;
import ru.windcorp.progressia.common.util.Matrices;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.DefaultChunkData;
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
		float radius = getServer().getLoadDistance(this) / Units.get(DefaultChunkData.BLOCKS_PER_CHUNK, "m");

		float radiusSq = radius * radius;
		int iRadius = (int) Math.ceil(radius);

		// The sphere around the player is stretched by this factor vertically
		// (along the player's up vector)
		final float verticalStretching = 0.4f;
		
		float factor = (1/verticalStretching - 1);
		Vec3 up = getServer().getWorld().getData().getGravityModel().getUp(getEntity().getPosition(), null);
		
		Mat3 transform = Matrices.grab3();
		
		//@formatter:off
		transform.set(
			1 + factor * up.x * up.x,   0 + factor * up.x * up.y,   0 + factor * up.x * up.z,
			0 + factor * up.y * up.x,   1 + factor * up.y * up.y,   0 + factor * up.y * up.z,
			0 + factor * up.z * up.x,   0 + factor * up.z * up.y,   1 + factor * up.z * up.z
		);
		//@formatter:on
		
		Vec3 transformedCursor = Vectors.grab3();
		
		for (cursor.x = -iRadius; cursor.x <= +iRadius; ++cursor.x) {
			for (cursor.y = -iRadius; cursor.y <= +iRadius; ++cursor.y) {
				for (cursor.z = -iRadius; cursor.z <= +iRadius; ++cursor.z) {
					
					transformedCursor.set(cursor.x, cursor.y, cursor.z);
					
					// .mul(Vec3) is cursed
					transform.mul(transformedCursor, transformedCursor);
					
					if (transformedCursor.dot(transformedCursor) <= radiusSq) {

						cursor.add(start);
						chunkConsumer.accept(cursor);
						cursor.sub(start);

					}
				}
			}
		}
		
		Matrices.release(transform);
		Vectors.release(transformedCursor);
	}

	public String getLogin() {
		return getClient().getLogin();
	}

}
