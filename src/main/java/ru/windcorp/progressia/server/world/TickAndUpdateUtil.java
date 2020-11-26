package ru.windcorp.progressia.server.world;

import java.util.List;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.LowOverheadCache;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.BlockTickContext;
import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.block.UpdateableBlock;
import ru.windcorp.progressia.server.world.tile.TickableTile;
import ru.windcorp.progressia.server.world.tile.TileLogic;
import ru.windcorp.progressia.server.world.tile.TileTickContext;
import ru.windcorp.progressia.server.world.tile.UpdateableTile;

public class TickAndUpdateUtil {
	
	private static final LowOverheadCache<MutableBlockTickContext> JAVAPONY_S_ULTIMATE_BLOCK_TICK_CONTEXT_SUPPLY =
			new LowOverheadCache<>(MutableBlockTickContext::new);
	
	private static final LowOverheadCache<MutableTileTickContext> JAVAPONY_S_ULTIMATE_TILE_TICK_CONTEXT_SUPPLY =
			new LowOverheadCache<>(MutableTileTickContext::new);
	
	public static void tickBlock(TickableBlock block, BlockTickContext context) {
		try {
			block.tick(context);
		} catch (Exception e) {
			CrashReports.report(e, "Could not tick block {}", block);
		}
	}
	
	public static void tickBlock(WorldLogic world, Vec3i blockInWorld) {
		BlockLogic block = world.getBlock(blockInWorld);
		if (!(block instanceof TickableBlock)) return; // also checks nulls
		
		BlockTickContext tickContext = grabBlockTickContext(world.getServer(), blockInWorld);
		tickBlock((TickableBlock) block, tickContext);
		releaseTickContext(tickContext);
	}
	
	public static void tickTile(TickableTile tile, TileTickContext context) {
		try {
			tile.tick(context);
		} catch (Exception e) {
			CrashReports.report(e, "Could not tick tile {}", tile);
		}
	}
	
	public static void tickTile(WorldLogic world, Vec3i blockInWorld, BlockFace face, int layer) {
		TileLogic tile = world.getTile(blockInWorld, face, layer);
		if (!(tile instanceof TickableTile)) return;
		
		TileTickContext tickContext = grabTileTickContext(world.getServer(), blockInWorld, face, layer);
		tickTile((TickableTile) tile, tickContext);
		releaseTickContext(tickContext);
	}
	
	public static void tickTiles(WorldLogic world, Vec3i blockInWorld, BlockFace face) {
		List<TileLogic> tiles = world.getTilesOrNull(blockInWorld, face);
		if (tiles == null || tiles.isEmpty()) return;
		
		MutableTileTickContext tickContext = JAVAPONY_S_ULTIMATE_TILE_TICK_CONTEXT_SUPPLY.grab();
		
		for (int layer = 0; layer < tiles.size(); ++layer) {
			TileLogic tile = tiles.get(layer);
			if (!(tile instanceof TickableTile)) return;
			
			tickContext.init(world.getServer(), blockInWorld, face, layer);
			tickTile((TickableTile) tile, tickContext);
		}
		
		JAVAPONY_S_ULTIMATE_TILE_TICK_CONTEXT_SUPPLY.release(tickContext);
	}
	
	public static void updateBlock(UpdateableBlock block, BlockTickContext context) {
		try {
			block.update(context);
		} catch (Exception e) {
			CrashReports.report(e, "Could not update block {}", block);
		}
	}
	
	public static void updateBlock(WorldLogic world, Vec3i blockInWorld) {
		BlockLogic block = world.getBlock(blockInWorld);
		if (!(block instanceof UpdateableBlock)) return; // also checks nulls
		
		BlockTickContext tickContext = grabBlockTickContext(world.getServer(), blockInWorld);
		updateBlock((UpdateableBlock) block, tickContext);
		releaseTickContext(tickContext);
	}
	
	public static void updateTile(UpdateableTile tile, TileTickContext context) {
		try {
			tile.update(context);
		} catch (Exception e) {
			CrashReports.report(e, "Could not update tile {}", tile);
		}
	}
	
	public static void updateTile(WorldLogic world, Vec3i blockInWorld, BlockFace face, int layer) {
		TileLogic tile = world.getTile(blockInWorld, face, layer);
		if (!(tile instanceof UpdateableTile)) return;

		TileTickContext tickContext = grabTileTickContext(world.getServer(), blockInWorld, face, layer);
		updateTile((UpdateableTile) tile, tickContext);
		releaseTickContext(tickContext);
	}
	
	public static void updateTiles(WorldLogic world, Vec3i blockInWorld, BlockFace face) {
		List<TileLogic> tiles = world.getTilesOrNull(blockInWorld, face);
		if (tiles == null || tiles.isEmpty()) return;
		
		MutableTileTickContext tickContext = JAVAPONY_S_ULTIMATE_TILE_TICK_CONTEXT_SUPPLY.grab();
		
		for (int layer = 0; layer < tiles.size(); ++layer) {
			TileLogic tile = tiles.get(layer);
			if (!(tile instanceof UpdateableTile)) return;
			
			tickContext.init(world.getServer(), blockInWorld, face, layer);
			updateTile((UpdateableTile) tile, tickContext);
		}
		
		JAVAPONY_S_ULTIMATE_TILE_TICK_CONTEXT_SUPPLY.release(tickContext);
	}
	
	public static BlockTickContext grabBlockTickContext(
			Server server,
			Vec3i blockInWorld
	) {
		MutableBlockTickContext result = JAVAPONY_S_ULTIMATE_BLOCK_TICK_CONTEXT_SUPPLY.grab();
		result.init(server, blockInWorld);
		return result;
	}
	
	public static TileTickContext grabTileTickContext(
			Server server,
			Vec3i blockInWorld,
			BlockFace face,
			int layer
	) {
		MutableTileTickContext result = JAVAPONY_S_ULTIMATE_TILE_TICK_CONTEXT_SUPPLY.grab();
		result.init(server, blockInWorld, face, layer);
		return result;
	}
	
	public static void releaseTickContext(BlockTickContext context) {
		JAVAPONY_S_ULTIMATE_BLOCK_TICK_CONTEXT_SUPPLY.release(
				(MutableBlockTickContext) context
		);
	}
	
	public static void releaseTickContext(TileTickContext context) {
		JAVAPONY_S_ULTIMATE_TILE_TICK_CONTEXT_SUPPLY.release(
				(MutableTileTickContext) context
		);
	}
	
	private TickAndUpdateUtil() {}

}
