package ru.windcorp.progressia.server.world.block;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.ChunkLogic;

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
	public double getTickLength() {
		return parent.getTickLength();
	}

	@Override
	public Server getServer() {
		return parent.getServer();
	}

	@Override
	public Vec3i getBlockInWorld() {
		return parent.getBlockInWorld();
	}

}
