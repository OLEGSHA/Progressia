package ru.windcorp.progressia.server.world.tile;

public interface TickableTile {

	void tick(TileTickContext context);

	default boolean doesTickRegularly(TileTickContext context) {
		return false;
	}

	default boolean doesTickRandomly(TileTickContext context) {
		return false;
	}

}