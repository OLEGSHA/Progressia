package ru.windcorp.progressia.server.world;

import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.block.Tickable;

public class Ticker implements Runnable {
	
	private final ImplementedChangeTracker tracker =
			new ImplementedChangeTracker();
	
	private final MutableBlockTickContext blockTickContext =
			new MutableBlockTickContext();
	
	private final Server server;

	public Ticker(Server server) {
		this.server = server;
	}

	@Override
	public void run() {
		this.blockTickContext.setTickLength(1000 / 20);
		server.getWorld().getChunks().forEach(chunk -> {
			tickChunk(chunk);
		});
	}

	private void tickChunk(ChunkLogic chunk) {
		MutableBlockTickContext context = this.blockTickContext;
		
		context.setServer(server);
		context.setChunk(chunk);
		
		tickRegularBlocks(chunk, context);
		tickRandomBlocks(chunk, context);
		
		flushChanges(chunk);
	}

	private void tickRegularBlocks(
			ChunkLogic chunk,
			MutableBlockTickContext context
	) {
		chunk.forEachTickingBlock((blockInChunk, block) -> {
			context.setCoordsInChunk(blockInChunk);
			((Tickable) block).tick(context, tracker);
		});
	}

	private void tickRandomBlocks(
			ChunkLogic chunk,
			MutableBlockTickContext context
	) {
		// TODO implement
	}

	private void flushChanges(ChunkLogic chunk) {
		this.tracker.applyChanges(server);
	}

}
