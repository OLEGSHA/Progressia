package ru.windcorp.progressia.server.world;

import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.entity.EntityLogic;
import ru.windcorp.progressia.server.world.entity.EntityLogicRegistry;
import ru.windcorp.progressia.server.world.tile.TickableTile;

public class Ticker implements Runnable {
	
	private final ImplementedChangeTracker tracker =
			new ImplementedChangeTracker();
	
	private final MutableBlockTickContext blockTickContext =
			new MutableBlockTickContext();
	
	private final MutableTileTickContext tileTickContext =
			new MutableTileTickContext();
	
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
		MutableBlockTickContext blockContext = this.blockTickContext;
		MutableTileTickContext tileContext = this.tileTickContext;
		
		blockContext.setServer(server);
		tileContext.setServer(server);
		
		blockContext.setChunk(chunk);
		tileContext.setChunk(chunk);
		
		tickRegularTickers(chunk, blockContext, tileContext);
		tickRandomBlocks(chunk, blockContext, tileContext);
		
		tickEntities(chunk, blockContext);
		
		flushChanges(chunk);
	}

	private void tickEntities(
			ChunkLogic chunk,
			MutableChunkTickContext tickContext
	) {
		// TODO this is ugly
		
		chunk.getData().getEntities().forEach(entity -> {
			EntityLogic logic = EntityLogicRegistry.getInstance().get(
					entity.getId()
			);
			
			logic.tick(entity, tickContext, tracker);
		});
	}

	private void tickRegularTickers(
			ChunkLogic chunk,
			MutableBlockTickContext blockContext,
			MutableTileTickContext tileContext
	) {
		chunk.forEachTickingBlock((blockInChunk, block) -> {
			blockContext.setCoordsInChunk(blockInChunk);
			((TickableBlock) block).tick(blockContext, tracker);
		});
		
		chunk.forEachTickingTile((locInChunk, tile) -> {
			tileContext.setCoordsInChunk(locInChunk.pos);
			tileContext.setFace(locInChunk.face);
			tileContext.setLayer(locInChunk.layer);
			
			((TickableTile) tile).tick(tileContext, tracker);
		});
	}

	private void tickRandomBlocks(
			ChunkLogic chunk,
			MutableBlockTickContext blockContext,
			MutableTileTickContext tileContext
	) {
		// TODO implement
	}

	private void flushChanges(ChunkLogic chunk) {
		this.tracker.applyChanges(server);
	}

}
