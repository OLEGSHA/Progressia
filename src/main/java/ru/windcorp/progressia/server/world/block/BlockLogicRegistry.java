package ru.windcorp.progressia.server.world.block;

import ru.windcorp.progressia.common.util.namespaces.NamespacedInstanceRegistry;

public class BlockLogicRegistry extends NamespacedInstanceRegistry<BlockLogic> {
	
	private static final BlockLogicRegistry INSTANCE = new BlockLogicRegistry();
	
	public static BlockLogicRegistry getInstance() {
		return INSTANCE;
	}

}
