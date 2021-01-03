package ru.windcorp.progressia.common.world;

import glm.vec._3.i.Vec3i;

public abstract class PacketAffectChunk extends PacketAffectWorld {

	public PacketAffectChunk(String id) {
		super(id);
	}

	public abstract void getAffectedChunk(Vec3i output);

}
