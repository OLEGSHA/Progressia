package ru.windcorp.progressia.test;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.comms.controls.ControlData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;

public class ControlPlaceTileData extends ControlData {
	
	private TileData tile;
	private final Vec3i blockInWorld = new Vec3i();
	private BlockFace face;

	public ControlPlaceTileData(String id) {
		super(id);
	}
	
	public TileData getTile() {
		return tile;
	}
	
	public Vec3i getBlockInWorld() {
		return blockInWorld;
	}
	
	public BlockFace getFace() {
		return face;
	}
	
	public void set(TileData block, Vec3i blockInWorld, BlockFace face) {
		this.tile = block;
		this.blockInWorld.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
		this.face = face;
	}

}
