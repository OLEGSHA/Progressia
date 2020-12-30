package ru.windcorp.progressia.common.world;

import glm.vec._3.i.Vec3i;

public abstract class PacketChunkChange extends PacketWorldChange {

	public PacketChunkChange(String id) {
		super(id);
	}

	public abstract void getAffectedChunk(Vec3i output);

}
