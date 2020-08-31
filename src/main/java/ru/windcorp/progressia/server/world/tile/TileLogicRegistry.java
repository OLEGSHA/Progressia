package ru.windcorp.progressia.server.world.tile;

import ru.windcorp.progressia.common.util.NamespacedRegistry;

public class TileLogicRegistry extends NamespacedRegistry<TileLogic> {
	
	private static final TileLogicRegistry INSTANCE = new TileLogicRegistry();
	
	public static TileLogicRegistry getInstance() {
		return INSTANCE;
	}

}
