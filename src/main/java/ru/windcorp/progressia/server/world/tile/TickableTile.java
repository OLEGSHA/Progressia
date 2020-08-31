package ru.windcorp.progressia.server.world.tile;

import ru.windcorp.progressia.server.world.Changer;

public interface TickableTile {

	void tick(TileTickContext context, Changer changer);

	default boolean doesTickRegularly(TileTickContext context) {
		return false;
	}

	default boolean doesTickRandomly(TileTickContext context) {
		return false;
	}

}