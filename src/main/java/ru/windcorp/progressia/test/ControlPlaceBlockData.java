package ru.windcorp.progressia.test;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.comms.controls.ControlData;
import ru.windcorp.progressia.common.world.block.BlockData;

public class ControlPlaceBlockData extends ControlData {
	
	private BlockData block;
	private final Vec3i blockInWorld = new Vec3i();

	public ControlPlaceBlockData(String id) {
		super(id);
	}
	
	public BlockData getBlock() {
		return block;
	}
	
	public Vec3i getBlockInWorld() {
		return blockInWorld;
	}
	
	public void set(BlockData block, Vec3i blockInWorld) {
		this.block = block;
		this.blockInWorld.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
	}

}
