package ru.windcorp.progressia.server.world;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.Coordinates;
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
		
		Vec3i chunk = Vectors.grab3i();
		Coordinates.convertInWorldToChunk(blockInWorld, chunk);
		setChunk(getWorld().getChunk(chunk));
		Vectors.release(chunk);
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
