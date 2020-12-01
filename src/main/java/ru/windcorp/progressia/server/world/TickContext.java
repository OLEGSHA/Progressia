package ru.windcorp.progressia.server.world;

import java.util.Random;

import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.tasks.WorldAccessor;

public interface TickContext {
	
	double getTickLength();
	
	Server getServer();
	
	default WorldLogic getWorld() {
		return getServer().getWorld();
	}
	
	default WorldAccessor getAccessor() {
		return getServer().getWorldAccessor();
	}
	
	default Random getRandom() {
		return getServer().getAdHocRandom();
	}
	
	default WorldData getWorldData() {
		return getWorld().getData();
	}

}
