package ru.windcorp.progressia.common.world.entity;

import ru.windcorp.progressia.common.state.StatefulObjectRegistry;

public class EntityDataRegistry extends StatefulObjectRegistry<EntityData> {
	
	private static final EntityDataRegistry INSTANCE = new EntityDataRegistry();
	
	public static EntityDataRegistry getInstance() {
		return INSTANCE;
	}
	
	public void register(String id) {
		super.register(id, () -> new EntityData(id));
	}

}
