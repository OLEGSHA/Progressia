package ru.windcorp.progressia.server.world.block;

import org.apache.logging.log4j.LogManager;

public interface UpdateableBlock {
	
	default void update(BlockTickContext context) {
		LogManager.getLogger().info("Updating block {} @ ({}; {}; {})", context.getBlock(), context.getBlockInWorld().x, context.getBlockInWorld().y, context.getBlockInWorld().z);
	}

}
