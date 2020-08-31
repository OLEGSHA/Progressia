package ru.windcorp.progressia.server.world.block;

import ru.windcorp.progressia.common.util.NamespacedRegistry;

public class BlockLogicRegistry extends NamespacedRegistry<BlockLogic> {
	
	private static final BlockLogicRegistry INSTANCE = new BlockLogicRegistry();
	
	public static BlockLogicRegistry getInstance() {
		return INSTANCE;
	}

}
