package ru.windcorp.progressia.server.world.entity;

import ru.windcorp.progressia.common.util.NamespacedRegistry;

public class EntityLogicRegistry extends NamespacedRegistry<EntityLogic> {
	
	private static final EntityLogicRegistry INSTANCE =
			new EntityLogicRegistry();
	
	public static EntityLogicRegistry getInstance() {
		return INSTANCE;
	}

}
