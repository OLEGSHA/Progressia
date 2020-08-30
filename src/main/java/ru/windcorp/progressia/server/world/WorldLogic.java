package ru.windcorp.progressia.server.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.WorldData;

public class WorldLogic {
	
	private final WorldData data;
	
	private final Map<ChunkData, ChunkLogic> chunks = new HashMap<>();
	
	public WorldLogic(WorldData data) {
		this.data = data;
		
		for (ChunkData chunkData : data.getChunks()) {
			chunks.put(chunkData, new ChunkLogic(this, chunkData));
		}
	}
	
	public WorldData getData() {
		return data;
	}
	
	public ChunkLogic getChunk(ChunkData chunkData) {
		return chunks.get(chunkData);
	}
	
	public ChunkLogic getChunk(Vec3i pos) {
		return chunks.get(getData().getChunk(pos));
	}
	
	public Collection<ChunkLogic> getChunks() {
		return chunks.values();
	}

}
