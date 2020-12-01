package ru.windcorp.progressia.server.world.tasks;

import java.util.List;
import java.util.Random;
import java.util.function.Consumer;

import com.google.common.collect.ImmutableList;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.FloatMathUtils;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.ChunkLogic;
import ru.windcorp.progressia.server.world.MutableBlockTickContext;
import ru.windcorp.progressia.server.world.MutableTileTickContext;
import ru.windcorp.progressia.server.world.TickAndUpdateUtil;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.BlockTickContext;
import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.ticking.Evaluation;
import ru.windcorp.progressia.server.world.ticking.TickingPolicy;
import ru.windcorp.progressia.server.world.tile.TickableTile;
import ru.windcorp.progressia.server.world.tile.TileLogic;
import ru.windcorp.progressia.server.world.tile.TileLogicRegistry;

import static ru.windcorp.progressia.common.world.ChunkData.BLOCKS_PER_CHUNK;

public class TickChunk extends Evaluation {
	
	private static final int CHUNK_VOLUME =
			ChunkData.BLOCKS_PER_CHUNK *
			ChunkData.BLOCKS_PER_CHUNK *
			ChunkData.BLOCKS_PER_CHUNK;
	
	private final List<Consumer<Server>> randomTickMethods = ImmutableList.of(
			s -> this.tickRandomBlock(s),
			s -> this.tickRandomTile(s, BlockFace.NORTH),
			s -> this.tickRandomTile(s, BlockFace.TOP),
			s -> this.tickRandomTile(s, BlockFace.WEST)
	);
	
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
		float ticks = computeRandomTicks(server);
		
		/*
		 * If we are expected to run 3.25 random ticks per tick
		 * on average, then run 3 random ticks unconditionally
		 * and run one extra random tick with 0.25 chance
		 */
		float unconditionalTicks = FloatMathUtils.floor(ticks);
		float extraTickChance = ticks - unconditionalTicks;
		
		for (int i = 0; i < unconditionalTicks; ++i) {
			tickRandomOnce(server);
		}
		
		if (server.getAdHocRandom().nextFloat() < extraTickChance) {
			tickRandomOnce(server);
		}
	}
	
	private void tickRandomOnce(Server server) {
		// Pick a target at random: a block or one of 3 primary block faces
		randomTickMethods.get(
				server.getAdHocRandom().nextInt(randomTickMethods.size())
		).accept(server);
	}
	
	private void tickRandomBlock(Server server) {
		Random random = server.getAdHocRandom();
		
		Vec3i blockInChunk = new Vec3i(
				random.nextInt(BLOCKS_PER_CHUNK),
				random.nextInt(BLOCKS_PER_CHUNK),
				random.nextInt(BLOCKS_PER_CHUNK)
		);

		BlockLogic block = this.chunk.getBlock(blockInChunk);
		
		if (!(block instanceof TickableBlock)) return;
		TickableBlock tickable = (TickableBlock) block;

		BlockTickContext context = TickAndUpdateUtil.getBlockTickContext(
				server,
				Coordinates.getInWorld(this.chunk.getPosition(), blockInChunk, null)
		);
		
		if (tickable.getTickingPolicy(context) != TickingPolicy.RANDOM) return;
		tickable.tick(context);
	}
	
	private void tickRandomTile(Server server, BlockFace face) {
		Random random = server.getAdHocRandom();
		
		Vec3i blockInChunk = new Vec3i(
				random.nextInt(BLOCKS_PER_CHUNK),
				random.nextInt(BLOCKS_PER_CHUNK),
				random.nextInt(BLOCKS_PER_CHUNK)
		);

		List<TileData> tiles = this.chunk.getData().getTilesOrNull(blockInChunk, face);
		if (tiles == null) return;
		
		MutableTileTickContext context = new MutableTileTickContext();
		Vec3i blockInWorld = Coordinates.getInWorld(this.chunk.getPosition(), blockInChunk, null);
		
		for (int layer = 0; layer < tiles.size(); ++layer) {
			TileData data = tiles.get(layer);
			
			TileLogic logic = TileLogicRegistry.getInstance().get(data.getId());
			if (!(logic instanceof TickableTile)) return;
			TickableTile tickable = (TickableTile) logic;
	
			context.init(server, blockInWorld, face, layer);
			
			if (tickable.getTickingPolicy(context) != TickingPolicy.RANDOM) return;
			tickable.tick(context);
		}
	}

	private float computeRandomTicks(Server server) {
		return (float) (
				server.getTickingSettings().getRandomTickFrequency() *
				CHUNK_VOLUME * randomTickMethods.size() *
				server.getTickLength()
		);
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
