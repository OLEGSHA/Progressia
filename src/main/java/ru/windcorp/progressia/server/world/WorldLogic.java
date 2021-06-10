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
import java.util.function.Function;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.ChunkDataListeners;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.WorldDataListener;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.GenericWorld;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.generation.WorldGenerator;
import ru.windcorp.progressia.server.world.tasks.TickEntitiesTask;
import ru.windcorp.progressia.server.world.ticking.Evaluation;
import ru.windcorp.progressia.server.world.tile.TileLogic;
import ru.windcorp.progressia.server.world.tile.TileLogicStack;

public class WorldLogic implements GenericWorld<BlockLogic, TileLogic, TileLogicStack, ChunkLogic, EntityData // not
																												// using
																												// EntityLogic
																												// because
																												// it
																												// is
																												// stateless
> {

	private final WorldData data;
	private final Server server;

	private final WorldGenerator generator;

	private final Map<ChunkData, ChunkLogic> chunks = new HashMap<>();

	private final Evaluation tickEntitiesTask = new TickEntitiesTask();

	public WorldLogic(WorldData data, Server server, Function<WorldLogic, WorldGenerator> worldGeneratorConstructor) {
		this.data = data;
		this.server = server;
		this.generator = worldGeneratorConstructor.apply(this);

		data.addListener(new WorldDataListener() {
			@Override
			public void onChunkLoaded(WorldData world, ChunkData chunk) {
				chunks.put(chunk, new ChunkLogic(WorldLogic.this, chunk));
			}

			@Override
			public void beforeChunkUnloaded(WorldData world, ChunkData chunk) {
				chunks.remove(chunk);
			}
		});

		data.addListener(ChunkDataListeners.createAdder(new UpdateTriggerer(server)));
	}

	@Override
	public ChunkLogic getChunk(Vec3i pos) {
		return chunks.get(getData().getChunk(pos));
	}

	@Override
	public Collection<ChunkLogic> getChunks() {
		return chunks.values();
	}

	@Override
	public Collection<EntityData> getEntities() {
		return getData().getEntities();
	}

	public Evaluation getTickEntitiesTask() {
		return tickEntitiesTask;
	}

	public Server getServer() {
		return server;
	}

	public WorldData getData() {
		return data;
	}

	public WorldGenerator getGenerator() {
		return generator;
	}

	public ChunkData generate(Vec3i chunkPos) {
		return getGenerator().generate(chunkPos, getData());
	}

	public ChunkLogic getChunk(ChunkData chunkData) {
		return chunks.get(chunkData);
	}

}
