package ru.windcorp.progressia.server.world.block;

import ru.windcorp.progressia.server.world.Changer;

public interface TickableBlock {

	void tick(BlockTickContext context, Changer changer);

	default boolean doesTickRegularly(BlockTickContext context) {
		return false;
	}

	default boolean doesTickRandomly(BlockTickContext context) {
		return false;
	}

}