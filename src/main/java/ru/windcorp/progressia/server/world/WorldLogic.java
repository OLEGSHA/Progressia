package ru.windcorp.progressia.server.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.WorldDataListener;
import ru.windcorp.progressia.server.world.block.BlockLogic;

public class WorldLogic {
	
	private final WorldData data;
	
	private final Map<ChunkData, ChunkLogic> chunks = new HashMap<>();
	
	public WorldLogic(WorldData data) {
		this.data = data;
		
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
	
	public ChunkLogic getChunkByBlock(Vec3i blockInWorld) {
		Vec3i chunkPos = Vectors.grab3i();
		Coordinates.convertInWorldToChunk(blockInWorld, chunkPos);
		ChunkLogic result = getChunk(chunkPos);
		Vectors.release(chunkPos);
		return result;
	}
	
	public BlockLogic getBlock(Vec3i blockInWorld) {
		ChunkLogic chunk = getChunkByBlock(blockInWorld);
		if (chunk == null) return null;
		
		Vec3i blockInChunk = Vectors.grab3i();
		Coordinates.convertInWorldToInChunk(blockInWorld, blockInChunk);
		BlockLogic result = chunk.getBlock(blockInChunk);
		
		return result;
	}
	
	public Collection<ChunkLogic> getChunks() {
		return chunks.values();
	}

}
