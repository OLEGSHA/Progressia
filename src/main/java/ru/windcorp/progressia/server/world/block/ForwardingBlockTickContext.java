package ru.windcorp.progressia.server.world.block;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.ChunkLogic;
import ru.windcorp.progressia.server.world.WorldLogic;

public class ForwardingBlockTickContext implements BlockTickContext {
	
	private BlockTickContext parent;
	
	public ForwardingBlockTickContext(BlockTickContext parent) {
		setParent(parent);
	}

	public BlockTickContext getParent() {
		return parent;
	}
	
	public void setParent(BlockTickContext parent) {
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
	public Vec3i getCoords() {
		return parent.getCoords();
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
	public Vec3i getChunkCoords() {
		return parent.getChunkCoords();
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
	public BlockLogic getBlock() {
		return parent.getBlock();
	}

	@Override
	public BlockData getBlockData() {
		return parent.getBlockData();
	}

}
