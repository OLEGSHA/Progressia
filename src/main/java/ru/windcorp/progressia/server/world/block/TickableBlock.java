package ru.windcorp.progressia.server.world.block;

import ru.windcorp.progressia.server.world.ticking.TickingPolicy;

public interface TickableBlock {

	void tick(BlockTickContext context);

	TickingPolicy getTickingPolicy(BlockTickContext context);

}