package ru.windcorp.progressia.server.world.tasks;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.TickAndUpdateUtil;
import ru.windcorp.progressia.server.world.ticking.Evaluation;

public class TickEntitiesTask extends Evaluation {

	@Override
	public void evaluate(Server server) {
		server.getWorld().forEachEntity(entity -> {
			TickAndUpdateUtil.tickEntity(entity, server);
		});
	}

	@Override
	public void getRelevantChunk(Vec3i output) {
		// Do nothing
	}
	
	@Override
	public boolean isThreadSensitive() {
		return false;
	}

}
