package ru.windcorp.progressia.server.world.block;

import ru.windcorp.progressia.server.world.Changer;

public interface Updatable {
	
	void update(BlockTickContext context, Changer changer);

}
