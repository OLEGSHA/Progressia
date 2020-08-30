package ru.windcorp.progressia.server.block;

import java.util.HashMap;
import java.util.Map;

public class BlockLogicRegistry {
	
	private static final Map<String, BlockLogic> REGISTRY = new HashMap<>();
	
	public static BlockLogic get(String name) {
		return REGISTRY.get(name);
	}
	
	public static void register(BlockLogic blockLogic) {
		REGISTRY.put(blockLogic.getId(), blockLogic);
	}

}
