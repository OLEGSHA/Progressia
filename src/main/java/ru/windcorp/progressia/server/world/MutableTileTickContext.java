package ru.windcorp.progressia.server.world;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.tile.TileTickContext;

public class MutableTileTickContext
extends MutableTickContext
implements TileTickContext {
	
	private final Vec3i currentBlockInWorld = new Vec3i();
	private final Vec3i counterBlockInWorld = new Vec3i();
	
	private BlockFace face;
	private int layer;
	
	@Override
	public Vec3i getCurrentBlockInWorld() {
		return this.currentBlockInWorld;
	}
	
	@Override
	public Vec3i getCounterBlockInWorld() {
		return this.counterBlockInWorld;
	}
	
	public void setCoordsInWorld(Vec3i currentBlockInWorld) {
		getCurrentBlockInWorld().set(currentBlockInWorld.x, currentBlockInWorld.y, currentBlockInWorld.z);
		getCounterBlockInWorld().set(currentBlockInWorld.x, currentBlockInWorld.y, currentBlockInWorld.z).add(getCurrentFace().getVector());
	}
	
	@Override
	public BlockFace getCurrentFace() {
		return face;
	}
	
	public void setFace(BlockFace face) {
		this.face = face;
		setCoordsInWorld(getCurrentBlockInWorld());
	}
	
	@Override
	public int getCurrentLayer() {
		return layer;
	}
	
	public void setLayer(int layer) {
		this.layer = layer;
	}
	
	public void init(Server server, Vec3i blockInWorld, BlockFace face, int layer) {
		setServer(server);
		setFace(face);
		setCoordsInWorld(blockInWorld);
		setLayer(layer);
	}
	
}
