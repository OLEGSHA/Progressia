package ru.windcorp.progressia.server.world.entity;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.world.TickContext;

public class EntityLogic extends Namespaced {

	public EntityLogic(String id) {
		super(id);
	}
	
	public void tick(EntityData entity, TickContext context) {
		entity.incrementAge(context.getTickLength());
	}

}
