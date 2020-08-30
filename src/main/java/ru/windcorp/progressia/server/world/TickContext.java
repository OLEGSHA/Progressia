package ru.windcorp.progressia.server.world;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.server.Server;

public interface TickContext {
	
	double getTickLength();
	
	Server getServer();
	
	default WorldLogic getWorld() {
		return getServer().getWorld();
	}
	
	default WorldData getWorldData() {
		return getWorld().getData();
	}
	
	void requestBlockTick(Vec3i blockInWorld);

}
