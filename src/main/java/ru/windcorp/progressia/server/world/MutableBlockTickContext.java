package ru.windcorp.progressia.server.world;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.block.BlockTickContext;

public class MutableBlockTickContext implements BlockTickContext {
	
	private double tickLength;
	
	private Server server;
	private WorldLogic world;
	
	private ChunkLogic chunk;
	
	private final Vec3i blockInWorld = new Vec3i();
	private final Vec3i blockInChunk = new Vec3i();
	
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
	public Vec3i getCoords() {
		return this.blockInWorld;
	}
	
	@Override
	public Vec3i getChunkCoords() {
		return this.blockInChunk;
	}
	
	public void setCoordsInWorld(Vec3i coords) {
		getCoords().set(coords.x, coords.y, coords.z);
		Coordinates.convertInWorldToInChunk(getCoords(), getChunkCoords());
		
		Vec3i chunk = Vectors.grab3i();
		Coordinates.convertInWorldToChunk(coords, chunk);
		setChunk(getWorld().getChunk(chunk));
		Vectors.release(chunk);
	}
	
	public void setCoordsInChunk(Vec3i coords) {
		getChunkCoords().set(coords.x, coords.y, coords.z);
		Coordinates.getInWorld(
				getChunkData().getPosition(), getChunkCoords(),
				getCoords()
		);
	}
	
	@Override
	public void requestBlockTick(Vec3i blockInWorld) {
		// TODO implement
		throw new UnsupportedOperationException("Not yet implemented");
	}
	
}
