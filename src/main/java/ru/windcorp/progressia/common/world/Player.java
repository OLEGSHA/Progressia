package ru.windcorp.progressia.common.world;

import ru.windcorp.progressia.common.world.entity.EntityData;

public class Player {
	
	private EntityData entity;

	public Player(EntityData entity) {
		this.entity = entity;
	}
	
	public EntityData getEntity() {
		return entity;
	}

}
