package ru.windcorp.progressia.server.comms;

import glm.vec._3.i.Vec3i;

public abstract class ClientPlayer extends Client {

	public ClientPlayer(int id) {
		super(id);
	}
	
	public abstract String getLogin();
	
	public boolean canSeeChunk(Vec3i chunkPos) {
		return true;
	}
	
	public boolean canSeeEntity(long entityId) {
		return true;
	}

}
