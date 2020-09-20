package ru.windcorp.progressia.server.world;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.world.tile.TileTickContext;

public class MutableTileTickContext
extends MutableChunkTickContext
implements TileTickContext {
	
	private final Vec3i blockInWorld = new Vec3i();
	private final Vec3i blockInChunk = new Vec3i();
	
	private BlockFace face;
	private int layer;
	
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
	public BlockFace getFace() {
		return face;
	}
	
	public void setFace(BlockFace face) {
		this.face = face;
	}
	
	@Override
	public int getLayer() {
		return layer;
	}
	
	public void setLayer(int layer) {
		this.layer = layer;
	}
	
}
