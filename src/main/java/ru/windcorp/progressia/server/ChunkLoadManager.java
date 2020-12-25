package ru.windcorp.progressia.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import glm.vec._3.i.Vec3i;
import gnu.trove.set.hash.TLongHashSet;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.generic.ChunkSet;
import ru.windcorp.progressia.common.world.generic.LongBasedChunkSet;
import ru.windcorp.progressia.test.TestContent;

public class ChunkLoadManager {
	
	private final Server server;
	
	private final Collection<Collection<? extends ChunkLoader>> allChunkLoaders =
			Collections.synchronizedCollection(new ArrayList<>());
	
	private final ChunkSet requested = new LongBasedChunkSet(new TLongHashSet());
	private final ChunkSet toLoad = new LongBasedChunkSet(new TLongHashSet());
	private final ChunkSet toUnload = new LongBasedChunkSet(new TLongHashSet());

	public ChunkLoadManager(Server server) {
		this.server = server;
		allChunkLoaders.add(server.getPlayerManager().getPlayers());
	}
	
	public void tick() {
		gatherRequests();
		updateQueues();
		processQueues();
	}

	private void gatherRequests() {
		requested.clear();
		
		allChunkLoaders.forEach(collection -> {
			collection.forEach(this::gatherRequests);
		});
	}
	
	private void gatherRequests(ChunkLoader loader) {
		loader.requestChunksToLoad(requested::add);
	}
	
	private void updateQueues() {
		ChunkSet loaded = getServer().getWorld().getData().getLoadedChunks();
		
		toLoad.clear();
		toLoad.addAll(requested);
		toLoad.removeAll(loaded);
		
		toUnload.clear();
		toUnload.addAll(loaded);
		toUnload.removeAll(requested);
	}

	private void processQueues() {
		toUnload.forEach(this::unloadChunk);
		toLoad.forEach(this::loadChunk);
	}

	public Server getServer() {
		return server;
	}
	
	public void loadChunk(Vec3i pos) {
		
		ChunkData chunk = new ChunkData(pos, getServer().getWorld().getData());
		TestContent.generateChunk(chunk);
		getServer().getWorld().getData().addChunk(chunk);
		
	}
	
	public void unloadChunk(Vec3i pos) {
		
		getServer().getWorld().getData().removeChunk(getServer().getWorld().getData().getChunk(pos));
		
	}

}
