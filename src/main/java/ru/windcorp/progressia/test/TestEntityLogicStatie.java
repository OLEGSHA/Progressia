package ru.windcorp.progressia.test;

import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.world.TickContext;
import ru.windcorp.progressia.server.world.entity.EntityLogic;

public class TestEntityLogicStatie extends EntityLogic {
	
	public TestEntityLogicStatie(String id) {
		super(id);
	}
	
	@Override
	public void tick(EntityData entity, TickContext context) {
		super.tick(entity, context);
		
		TestEntityDataStatie statie = (TestEntityDataStatie) entity;
		
		int size = (int) (18 + 6 * Math.sin(entity.getAge()));
		context.getServer().getWorldAccessor().changeEntity(statie, e -> e.setSizeNow(size));
	}

}
