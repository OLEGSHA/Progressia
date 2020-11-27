package ru.windcorp.progressia.server.world;

import java.util.List;

import glm.vec._3.i.Vec3i;
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
		
		BlockTickContext tickContext = getBlockTickContext(world.getServer(), blockInWorld);
		tickBlock((TickableBlock) block, tickContext);
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
		
		TileTickContext tickContext = getTileTickContext(world.getServer(), blockInWorld, face, layer);
		tickTile((TickableTile) tile, tickContext);
	}
	
	public static void tickTiles(WorldLogic world, Vec3i blockInWorld, BlockFace face) {
		List<TileLogic> tiles = world.getTilesOrNull(blockInWorld, face);
		if (tiles == null || tiles.isEmpty()) return;
		
		MutableTileTickContext tickContext = new MutableTileTickContext();
		
		for (int layer = 0; layer < tiles.size(); ++layer) {
			TileLogic tile = tiles.get(layer);
			if (!(tile instanceof TickableTile)) return;
			
			tickContext.init(world.getServer(), blockInWorld, face, layer);
			tickTile((TickableTile) tile, tickContext);
		}
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
		
		BlockTickContext tickContext = getBlockTickContext(world.getServer(), blockInWorld);
		updateBlock((UpdateableBlock) block, tickContext);
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

		TileTickContext tickContext = getTileTickContext(world.getServer(), blockInWorld, face, layer);
		updateTile((UpdateableTile) tile, tickContext);
	}
	
	public static void updateTiles(WorldLogic world, Vec3i blockInWorld, BlockFace face) {
		List<TileLogic> tiles = world.getTilesOrNull(blockInWorld, face);
		if (tiles == null || tiles.isEmpty()) return;
		
		MutableTileTickContext tickContext = new MutableTileTickContext();
		
		for (int layer = 0; layer < tiles.size(); ++layer) {
			TileLogic tile = tiles.get(layer);
			if (!(tile instanceof UpdateableTile)) return;
			
			tickContext.init(world.getServer(), blockInWorld, face, layer);
			updateTile((UpdateableTile) tile, tickContext);
		}
	}
	
	public static BlockTickContext getBlockTickContext(
			Server server,
			Vec3i blockInWorld
	) {
		MutableBlockTickContext result = new MutableBlockTickContext();
		result.init(server, blockInWorld);
		return result;
	}
	
	public static TileTickContext getTileTickContext(
			Server server,
			Vec3i blockInWorld,
			BlockFace face,
			int layer
	) {
		MutableTileTickContext result = new MutableTileTickContext();
		result.init(server, blockInWorld, face, layer);
		return result;
	}
	
	private TickAndUpdateUtil() {}

}
