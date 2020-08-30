package ru.windcorp.progressia.server.world;

import ru.windcorp.progressia.common.world.ChunkData;

public interface ChunkTickContext extends TickContext {
	
	ChunkLogic getChunk();
	
	default ChunkData getChunkData() {
		return getChunk().getData();
	}

}
