package ru.windcorp.progressia.server.world.tile;

import ru.windcorp.progressia.common.util.namespaces.NamespacedInstanceRegistry;

public class TileLogicRegistry extends NamespacedInstanceRegistry<TileLogic> {
	
	private static final TileLogicRegistry INSTANCE = new TileLogicRegistry();
	
	public static TileLogicRegistry getInstance() {
		return INSTANCE;
	}

}
