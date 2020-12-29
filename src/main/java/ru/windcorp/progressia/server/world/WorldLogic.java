package ru.windcorp.progressia.server.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.ChunkDataListeners;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.WorldDataListener;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.GenericWorld;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.tasks.TickEntitiesTask;
import ru.windcorp.progressia.server.world.ticking.Evaluation;
import ru.windcorp.progressia.server.world.tile.TileLogic;
import ru.windcorp.progressia.server.world.tile.TileLogicStack;

public class WorldLogic
implements GenericWorld<
	BlockLogic,
	TileLogic,
	TileLogicStack,
	ChunkLogic,
	EntityData // not using EntityLogic because it is stateless
> {
	
	private final WorldData data;
	private final Server server;
	
	private final Map<ChunkData, ChunkLogic> chunks = new HashMap<>();
	
	private final Evaluation tickEntitiesTask = new TickEntitiesTask();
	
	public WorldLogic(WorldData data, Server server) {
		this.data = data;
		this.server = server;
		
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
	
	public ChunkLogic getChunk(ChunkData chunkData) {
		return chunks.get(chunkData);
	}

}
