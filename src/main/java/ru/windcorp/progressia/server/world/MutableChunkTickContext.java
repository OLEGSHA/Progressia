package ru.windcorp.progressia.server.world;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.Server;

public class MutableChunkTickContext implements ChunkTickContext {

	private double tickLength;
	private Server server;
	private WorldLogic world;
	private ChunkLogic chunk;

	public MutableChunkTickContext() {
		super();
	}

	public double getTickLength() {
		return tickLength;
	}

	public void setTickLength(double tickLength) {
		this.tickLength = tickLength;
	}

	@Override
	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
		setWorld(server.getWorld());
	}

	@Override
	public WorldLogic getWorld() {
		return world;
	}

	public void setWorld(WorldLogic world) {
		this.world = world;
	}

	@Override
	public ChunkLogic getChunk() {
		return chunk;
	}

	public void setChunk(ChunkLogic chunk) {
		this.chunk = chunk;
	}

	@Override
	public void requestBlockTick(Vec3i blockInWorld) {
		// TODO implement
		throw new UnsupportedOperationException("Not yet implemented");
	}

	@Override
	public void requestTileTick(Vec3i blockInWorld, BlockFace face, int layer) {
		// TODO implement
		throw new UnsupportedOperationException("Not yet implemented");
	}

}