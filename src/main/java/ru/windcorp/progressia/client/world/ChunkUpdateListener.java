package ru.windcorp.progressia.client.world;

import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.ChunkDataListener;

class ChunkUpdateListener implements ChunkDataListener {
	
	private final WorldRender world;
	
	public ChunkUpdateListener(WorldRender world) {
		this.world = world;
	}

	@Override
	public void onChunkChanged(ChunkData chunk) {
		world.getChunk(chunk).markForUpdate();
	}

}
