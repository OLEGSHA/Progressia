package ru.windcorp.progressia.common.world.entity;

import ru.windcorp.progressia.common.util.NamespacedRegistry;

public class EntityDataRegistry extends NamespacedRegistry<EntityData> {
	
	private static final EntityDataRegistry INSTANCE = new EntityDataRegistry();
	
	public static EntityDataRegistry getInstance() {
		return INSTANCE;
	}

}
