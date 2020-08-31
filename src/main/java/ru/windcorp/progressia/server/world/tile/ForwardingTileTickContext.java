package ru.windcorp.progressia.server.world.tile;

import java.util.List;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.ChunkLogic;
import ru.windcorp.progressia.server.world.WorldLogic;
import ru.windcorp.progressia.server.world.block.BlockLogic;

public class ForwardingTileTickContext implements TileTickContext {
	
	private TileTickContext parent;

	public ForwardingTileTickContext(TileTickContext parent) {
		this.parent = parent;
	}
	
	public TileTickContext getParent() {
		return parent;
	}
	
	public void setParent(TileTickContext parent) {
		this.parent = parent;
	}

	@Override
	public ChunkLogic getChunk() {
		return parent.getChunk();
	}

	@Override
	public ChunkData getChunkData() {
		return parent.getChunkData();
	}

	@Override
	public double getTickLength() {
		return parent.getTickLength();
	}

	@Override
	public Server getServer() {
		return parent.getServer();
	}

	@Override
	public WorldLogic getWorld() {
		return parent.getWorld();
	}

	@Override
	public WorldData getWorldData() {
		return parent.getWorldData();
	}

	@Override
	public void requestBlockTick(Vec3i blockInWorld) {
		parent.requestBlockTick(blockInWorld);
	}

	@Override
	public void requestTileTick(Vec3i blockInWorld, BlockFace face, int layer) {
		parent.requestTileTick(blockInWorld, face, layer);
	}

	@Override
	public Vec3i getCoords() {
		return parent.getCoords();
	}

	@Override
	public Vec3i getChunkCoords() {
		return parent.getChunkCoords();
	}

	@Override
	public BlockFace getFace() {
		return parent.getFace();
	}

	@Override
	public int getLayer() {
		return parent.getLayer();
	}

	@Override
	public TileLogic getTile() {
		return parent.getTile();
	}

	@Override
	public TileData getTileData() {
		return parent.getTileData();
	}

	@Override
	public List<TileLogic> getTiles() {
		return parent.getTiles();
	}

	@Override
	public List<TileLogic> getTilesOrNull() {
		return parent.getTilesOrNull();
	}

	@Override
	public List<TileData> getTileDataList() {
		return parent.getTileDataList();
	}

	@Override
	public List<TileData> getTileDataListOrNull() {
		return parent.getTileDataListOrNull();
	}

	@Override
	public BlockLogic getBlock() {
		return parent.getBlock();
	}

	@Override
	public BlockData getBlockData() {
		return parent.getBlockData();
	}

}
