package ru.windcorp.progressia.server.world.tile;

import ru.windcorp.progressia.server.world.Changer;

public interface UpdatableTile {
	
	void update(TileTickContext context, Changer changer);

}
