package ru.windcorp.progressia.test;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.comms.controls.ControlData;

public class ControlBreakBlockData extends ControlData {
	
	private final Vec3i blockInWorld = new Vec3i();

	public ControlBreakBlockData(String id) {
		super(id);
	}
	
	public Vec3i getBlockInWorld() {
		return blockInWorld;
	}
	
	public void setBlockInWorld(Vec3i blockInWorld) {
		this.blockInWorld.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
	}

}
