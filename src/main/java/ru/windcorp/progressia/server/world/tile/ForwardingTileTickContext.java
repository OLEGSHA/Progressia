package ru.windcorp.progressia.server.world.tile;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.WorldLogic;

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
	public Vec3i getCurrentBlockInWorld() {
		return parent.getCurrentBlockInWorld();
	}
	
	@Override
	public Vec3i getCounterBlockInWorld() {
		return parent.getCounterBlockInWorld();
	}

	@Override
	public BlockFace getCurrentFace() {
		return parent.getCurrentFace();
	}

	@Override
	public int getCurrentLayer() {
		return parent.getCurrentLayer();
	}

}
