package ru.windcorp.progressia.server.world;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.BiConsumer;

import com.google.common.collect.Lists;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.world.tile.TileLocation;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.BlockLogicRegistry;
import ru.windcorp.progressia.server.world.block.TickableBlock;
import ru.windcorp.progressia.server.world.tile.TickableTile;
import ru.windcorp.progressia.server.world.tile.TileLogic;
import ru.windcorp.progressia.server.world.tile.TileLogicRegistry;

public class ChunkLogic {
	
	private final WorldLogic world;
	private final ChunkData data;
	
	private final Collection<Vec3i> tickingBlocks = new ArrayList<>();
	private final Collection<TileLocation> tickingTiles = new ArrayList<>();
	
	private final Map<List<TileData>, List<TileLogic>> tileLogicLists =
			Collections.synchronizedMap(new WeakHashMap<>());
	
	public ChunkLogic(WorldLogic world, ChunkData data) {
		this.world = world;
		this.data = data;
		
		generateTickLists();
	}
	
	private void generateTickLists() {
		MutableBlockTickContext blockTickContext =
				new MutableBlockTickContext();
		
		MutableTileTickContext tileTickContext =
				new MutableTileTickContext();
		
		data.forEachBlock(blockInChunk -> {
			BlockLogic block = getBlock(blockInChunk);
			
			if (block instanceof TickableBlock) {
				Vec3i blockInWorld = Vectors.grab3i();
				Coordinates.getInWorld(getData().getPosition(), blockInChunk, blockInWorld);
				
				blockTickContext.init(getWorld().getServer(), blockInWorld);

				Vectors.release(blockInWorld);
				
				if (((TickableBlock) block).doesTickRegularly(blockTickContext)) {
					tickingBlocks.add(new Vec3i(blockInChunk));
				}
			}
		});
		
		data.forEachTile((loc, tileData) -> {
			TileLogic tile = TileLogicRegistry.getInstance().get(tileData.getId());
			
			if (tile instanceof TickableTile) {
				Vec3i blockInWorld = Vectors.grab3i();
				Coordinates.getInWorld(getData().getPosition(), loc.pos, blockInWorld);
				
				tileTickContext.init(getWorld().getServer(), blockInWorld, loc.face, loc.layer);
				
				Vectors.release(blockInWorld);
				
				if (((TickableTile) tile).doesTickRegularly(tileTickContext)) {
					tickingTiles.add(new TileLocation(loc));
				}
			}
		});
	}

	public WorldLogic getWorld() {
		return world;
	}
	
	public ChunkData getData() {
		return data;
	}
	
	public boolean hasTickingBlocks() {
		return !tickingBlocks.isEmpty();
	}
	
	public boolean hasTickingTiles() {
		return !tickingTiles.isEmpty();
	}
	
	public void forEachTickingBlock(BiConsumer<Vec3i, BlockLogic> action) {
		tickingBlocks.forEach(blockInChunk -> {
			action.accept(blockInChunk, getBlock(blockInChunk));
		});
	}
	
	public void forEachTickingTile(BiConsumer<TileLocation, TileLogic> action) {
		tickingTiles.forEach(location -> {
			action.accept(
					location,
					getTilesOrNull(location.pos, location.face).get(location.layer)
			);
		});
	}
	
	public BlockLogic getBlock(Vec3i blockInChunk) {
		return BlockLogicRegistry.getInstance().get(
				getData().getBlock(blockInChunk).getId()
		);
	}
	
	public List<TileLogic> getTiles(Vec3i blockInChunk, BlockFace face) {
		return wrapTileList(getData().getTiles(blockInChunk, face));
	}
	
	public List<TileLogic> getTilesOrNull(Vec3i blockInChunk, BlockFace face) {
		List<TileData> tiles = getData().getTilesOrNull(blockInChunk, face);
		if (tiles == null) return null;
		return wrapTileList(tiles);
	}
	
	private List<TileLogic> wrapTileList(List<TileData> tileDataList) {
		return tileLogicLists.computeIfAbsent(
				tileDataList,
				ChunkLogic::createWrapper
		);
	}
	
	private static List<TileLogic> createWrapper(List<TileData> tileDataList) {
		return Lists.transform(
				tileDataList,
				data -> TileLogicRegistry.getInstance().get(data.getId())
		);
	}

}
