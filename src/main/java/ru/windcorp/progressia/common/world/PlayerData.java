package ru.windcorp.progressia.common.world;

import ru.windcorp.progressia.common.world.entity.EntityData;

public class PlayerData {
	
	private EntityData entity;

	public PlayerData(EntityData entity) {
		this.entity = entity;
	}
	
	public EntityData getEntity() {
		return entity;
	}

}
