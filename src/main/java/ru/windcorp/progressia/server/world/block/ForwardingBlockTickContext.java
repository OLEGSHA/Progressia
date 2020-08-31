package ru.windcorp.progressia.server.world.block;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.ChunkLogic;
import ru.windcorp.progressia.server.world.WorldLogic;

public class ForwardingBlockTickContext {
	
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

	public ChunkLogic getChunk() {
		return parent.getChunk();
	}

	public ChunkData getChunkData() {
		return parent.getChunkData();
	}

	public double getTickLength() {
		return parent.getTickLength();
	}

	public Server getServer() {
		return parent.getServer();
	}

	public Vec3i getCoords() {
		return parent.getCoords();
	}

	public WorldLogic getWorld() {
		return parent.getWorld();
	}

	public WorldData getWorldData() {
		return parent.getWorldData();
	}

	public Vec3i getChunkCoords() {
		return parent.getChunkCoords();
	}

	public void requestBlockTick(Vec3i blockInWorld) {
		parent.requestBlockTick(blockInWorld);
	}

	public BlockLogic getBlock() {
		return parent.getBlock();
	}

	public BlockData getBlockData() {
		return parent.getBlockData();
	}

}
