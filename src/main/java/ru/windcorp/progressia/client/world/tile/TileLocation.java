package ru.windcorp.progressia.client.world.tile;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class TileLocation {
	
	public final Vec3i pos = new Vec3i();
	public BlockFace face;
	public int layer;
	
	public TileLocation() {
		// Do nothing
	}
	
	public TileLocation(TileLocation src) {
		this.pos.set(src.pos.x, src.pos.y, src.pos.z);
		this.face = src.face;
		this.layer = src.layer;
	}

}
