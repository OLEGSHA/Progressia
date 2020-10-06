package ru.windcorp.progressia.server.world.entity;

import ru.windcorp.progressia.common.util.Namespaced;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.world.Changer;
import ru.windcorp.progressia.server.world.TickContext;

public class EntityLogic extends Namespaced {

	public EntityLogic(String namespace, String name) {
		super(namespace, name);
	}
	
	public void tick(EntityData entity, TickContext context, Changer changer) {
		entity.incrementAge(context.getTickLength());
	}

}
