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
 
package ru.windcorp.progressia.server.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import glm.Glm;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.ChunkDataListeners;
import ru.windcorp.progressia.common.world.DefaultWorldData;
import ru.windcorp.progressia.common.world.WorldDataListener;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.generation.WorldGenerator;
import ru.windcorp.progressia.server.world.tasks.TickEntitiesTask;
import ru.windcorp.progressia.server.world.ticking.Evaluation;

public class DefaultWorldLogic implements WorldLogic {

	private final DefaultWorldData data;
	private final Server server;

	private final WorldGenerator generator;

	private final Map<DefaultChunkData, DefaultChunkLogic> chunks = new HashMap<>();

	private final Evaluation tickEntitiesTask = new TickEntitiesTask();

	public DefaultWorldLogic(DefaultWorldData data, Server server, WorldGenerator generator) {
		this.data = data;
		this.server = server;
		
		this.generator = generator;
		data.setGravityModel(getGenerator().getGravityModel());

		data.addListener(new WorldDataListener() {
			@Override
			public void onChunkLoaded(DefaultWorldData world, DefaultChunkData chunk) {
				chunks.put(chunk, new DefaultChunkLogic(DefaultWorldLogic.this, chunk));
			}

			@Override
			public void beforeChunkUnloaded(DefaultWorldData world, DefaultChunkData chunk) {
				chunks.remove(chunk);
			}
		});

		data.addListener(ChunkDataListeners.createAdder(new UpdateTriggerer(server)));
	}

	@Override
	public DefaultChunkLogic getChunk(Vec3i pos) {
		return chunks.get(getData().getChunk(pos));
	}

	@Override
	public Collection<DefaultChunkLogic> getChunks() {
		return chunks.values();
	}

	@Override
	public Collection<EntityData> getEntities() {
		return getData().getEntities();
	}
	
	@Override
	public EntityData getEntity(long entityId) {
		return getData().getEntity(entityId);
	}

	public Evaluation getTickEntitiesTask() {
		return tickEntitiesTask;
	}

	public Server getServer() {
		return server;
	}

	@Override
	public DefaultWorldData getData() {
		return data;
	}

	public WorldGenerator getGenerator() {
		return generator;
	}

	public DefaultChunkData generate(Vec3i chunkPos) {
		DefaultChunkData chunk = getGenerator().generate(chunkPos);
		
		if (!Glm.equals(chunkPos, chunk.getPosition())) {
			throw CrashReports.report(null, "Generator %s has generated a chunk at (%d; %d; %d) when requested to generate a chunk at (%d; %d; %d)",
				getGenerator(),
				chunk.getX(), chunk.getY(), chunk.getZ(),
				chunkPos.x,   chunkPos.y,   chunkPos.z
			);
		}
		
		if (getData().getChunk(chunk.getPosition()) != chunk) {
			if (isChunkLoaded(chunkPos)) {
				throw CrashReports.report(null, "Generator %s has returned a chunk different to the chunk that is located at (%d; %d; %d)",
					getGenerator(),
					chunkPos.x, chunkPos.y, chunkPos.z
				);
			} else {
				throw CrashReports.report(null, "Generator %s has returned a chunk that is not loaded when requested to generate a chunk at (%d; %d; %d)",
					getGenerator(),
					chunkPos.x, chunkPos.y, chunkPos.z
				);
			}
		}
		
		if (!getChunk(chunk).isReady()) {
			throw CrashReports.report(null, "Generator %s has returned a chunk that is not ready when requested to generate a chunk at (%d; %d; %d)",
				getGenerator(),
				chunkPos.x, chunkPos.y, chunkPos.z
			);
		}
		
		return chunk;
	}

	public DefaultChunkLogic getChunk(DefaultChunkData chunkData) {
		return chunks.get(chunkData);
	}

}
