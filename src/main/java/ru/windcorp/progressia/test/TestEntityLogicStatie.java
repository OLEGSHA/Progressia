package ru.windcorp.progressia.test;

import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.world.Changer;
import ru.windcorp.progressia.server.world.TickContext;
import ru.windcorp.progressia.server.world.entity.EntityLogic;

public class TestEntityLogicStatie extends EntityLogic {
	
	public TestEntityLogicStatie() {
		super("Test", "Statie");
	}
	
	@Override
	public void tick(EntityData entity, TickContext context, Changer changer) {
		super.tick(entity, context, changer);
		
		TestEntityDataStatie statie = (TestEntityDataStatie) entity;
		
		int size = (int) (18 + 6 * Math.sin(entity.getAge()));
		changer.changeEntity(statie, e -> e.setSizeNow(size));
	}

}
