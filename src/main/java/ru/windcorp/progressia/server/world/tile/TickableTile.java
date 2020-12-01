package ru.windcorp.progressia.server.world.tile;

import ru.windcorp.progressia.server.world.ticking.TickingPolicy;

public interface TickableTile {

	void tick(TileTickContext context);

	TickingPolicy getTickingPolicy(TileTickContext context);

}