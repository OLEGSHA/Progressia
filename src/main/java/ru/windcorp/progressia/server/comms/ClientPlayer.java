package ru.windcorp.progressia.server.comms;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.test.TestContent;

public abstract class ClientPlayer extends Client {

	public ClientPlayer(int id) {
		super(id);
	}
	
	public abstract String getLogin();
	
	public boolean canSeeChunk(Vec3i chunkPos) {
		return true;
	}
	
	public boolean canSeeEntity(long entityId) {
		return entityId == TestContent.PLAYER_ENTITY_ID;
	}

}
