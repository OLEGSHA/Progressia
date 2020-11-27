package ru.windcorp.progressia.server.world;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.block.BlockTickContext;

public class MutableBlockTickContext
extends MutableTickContext
implements BlockTickContext {
	
	private final Vec3i blockInWorld = new Vec3i();
	private ChunkLogic chunk;
	
	@Override
	public Vec3i getBlockInWorld() {
		return this.blockInWorld;
	}
	
	public void setCoordsInWorld(Vec3i blockInWorld) {
		getBlockInWorld().set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
		setChunk(getWorld().getChunkByBlock(blockInWorld));
	}

	@Override
	public ChunkLogic getChunk() {
		return chunk;
	}

	public void setChunk(ChunkLogic chunk) {
		this.chunk = chunk;
	}
	
	public void init(Server server, Vec3i blockInWorld) {
		setServer(server);
		setCoordsInWorld(blockInWorld);
	}
	
}
