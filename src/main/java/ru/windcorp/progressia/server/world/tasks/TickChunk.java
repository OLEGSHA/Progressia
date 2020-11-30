package ru.windcorp.progressia.server.world.tasks;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.ChunkLogic;
import ru.windcorp.progressia.server.world.MutableBlockTickContext;
import ru.windcorp.progressia.server.world.MutableTileTickContext;
import ru.windcorp.progressia.server.world.TickAndUpdateUtil;
import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.ticking.Evaluation;
import ru.windcorp.progressia.server.world.tile.TickableTile;

public class TickChunk extends Evaluation {
	
	private final ChunkLogic chunk;

	public TickChunk(ChunkLogic chunk) {
		this.chunk = chunk;
	}

	@Override
	public void evaluate(Server server) {
		tickRegulars(server);
		tickRandom(server);
		tickEntities(server);
	}

	private void tickRegulars(Server server) {
		tickRegularBlocks(server);
		tickRegularTiles(server);
	}

	private void tickRegularBlocks(Server server) {
		if (!chunk.hasTickingBlocks()) return;
		
		MutableBlockTickContext context = new MutableBlockTickContext();
		Vec3i blockInWorld = new Vec3i();
		
		chunk.forEachTickingBlock((blockInChunk, block) -> {
			
			Coordinates.getInWorld(chunk.getPosition(), blockInChunk, blockInWorld);
			context.init(server, blockInWorld);
			((TickableBlock) block).tick(context);
			
		});
	}

	private void tickRegularTiles(Server server) {
		if (!chunk.hasTickingTiles()) return;
		
		MutableTileTickContext context = new MutableTileTickContext();
		Vec3i blockInWorld = new Vec3i();
		
		chunk.forEachTickingTile((loc, tile) -> {
			
			Coordinates.getInWorld(chunk.getPosition(), loc.pos, blockInWorld);
			context.init(server, blockInWorld, loc.face, loc.layer);
			((TickableTile) tile).tick(context);
			
		});
	}

	private void tickRandom(Server server) {
		// TODO Implement
	}

	private void tickEntities(Server server) {
		chunk.getData().forEachEntity(entity -> {
			TickAndUpdateUtil.tickEntity(entity, server);
		});
	}

	@Override
	public void getRelevantChunk(Vec3i output) {
		Vec3i p = chunk.getData().getPosition();
		output.set(p.x, p.y, p.z);
	}

}
