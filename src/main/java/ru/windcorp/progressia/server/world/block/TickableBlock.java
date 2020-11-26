package ru.windcorp.progressia.server.world.block;

public interface TickableBlock {

	void tick(BlockTickContext context);

	default boolean doesTickRegularly(BlockTickContext context) {
		return false;
	}

	default boolean doesTickRandomly(BlockTickContext context) {
		return false;
	}

}