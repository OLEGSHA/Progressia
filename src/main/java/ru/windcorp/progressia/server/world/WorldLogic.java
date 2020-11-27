package ru.windcorp.progressia.server.world;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.ChunkDataListener;
import ru.windcorp.progressia.common.world.ChunkDataListeners;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.WorldDataListener;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.tile.TileLogic;

public class WorldLogic {
	
	private final WorldData data;
	private final Server server;
	
	private final Map<ChunkData, ChunkLogic> chunks = new HashMap<>();
	
	public WorldLogic(WorldData data, Server server) {
		this.data = data;
		this.server = server;
		
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
		
		data.addListener(ChunkDataListeners.createAdder(new ChunkDataListener() {
			@Override
			public void onChunkBlockChanged(
					ChunkData chunk, Vec3i blockInChunk, BlockData previous, BlockData current
			) {
				getServer().getWorldAccessor().triggerUpdates(Coordinates.getInWorld(chunk.getPosition(), blockInChunk, null));
			}
			
			@Override
			public void onChunkTilesChanged(
					ChunkData chunk, Vec3i blockInChunk, BlockFace face, TileData tile,
					boolean wasAdded
			) {
				getServer().getWorldAccessor().triggerUpdates(Coordinates.getInWorld(chunk.getPosition(), blockInChunk, null), face);
			}
		}));
	}
	
	public Server getServer() {
		return server;
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
		return getChunk(Coordinates.convertInWorldToChunk(blockInWorld, null));
	}
	
	public BlockLogic getBlock(Vec3i blockInWorld) {
		ChunkLogic chunk = getChunkByBlock(blockInWorld);
		if (chunk == null) return null;
		
		return chunk.getBlock(Coordinates.convertInWorldToInChunk(blockInWorld, null));
	}
	
	public List<TileLogic> getTiles(Vec3i blockInWorld, BlockFace face) {
		return getTilesImpl(blockInWorld, face, true);
	}
	
	public List<TileLogic> getTilesOrNull(Vec3i blockInWorld, BlockFace face) {
		return getTilesImpl(blockInWorld, face, false);
	}
	
	private List<TileLogic> getTilesImpl(Vec3i blockInWorld, BlockFace face, boolean createIfMissing) {
		ChunkLogic chunk = getChunkByBlock(blockInWorld);
		if (chunk == null) return null;
		
		Vec3i blockInChunk = Coordinates.convertInWorldToInChunk(blockInWorld, null);
		
		List<TileLogic> result =
				createIfMissing
				? chunk.getTiles(blockInChunk, face)
				: chunk.getTilesOrNull(blockInChunk, face);
		
		return result;
	}
	
	public TileLogic getTile(Vec3i blockInWorld, BlockFace face, int layer) {
		List<TileLogic> tiles = getTilesOrNull(blockInWorld, face);
		if (tiles == null || tiles.size() <= layer) return null;
		
		return tiles.get(layer);
	}
	
	public Collection<ChunkLogic> getChunks() {
		return chunks.values();
	}

}
