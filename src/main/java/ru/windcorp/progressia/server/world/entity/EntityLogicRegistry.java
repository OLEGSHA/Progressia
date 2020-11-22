package ru.windcorp.progressia.server.world.entity;

import ru.windcorp.progressia.common.util.namespaces.NamespacedInstanceRegistry;

public class EntityLogicRegistry extends NamespacedInstanceRegistry<EntityLogic> {
	
	private static final EntityLogicRegistry INSTANCE =
			new EntityLogicRegistry();
	
	public static EntityLogicRegistry getInstance() {
		return INSTANCE;
	}

}
