package ru.windcorp.progressia.client.world.entity;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.generic.GenericEntity;

public abstract class EntityRenderable implements Renderable, GenericEntity {
	
	private final EntityData data;
	
	public EntityRenderable(EntityData data) {
		this.data = data;
	}

	public EntityData getData() {
		return data;
	}
	
	@Override
	public Vec3 getPosition() {
		return getData().getPosition();
	}
	
	@Override
	public String getId() {
		return getData().getId();
	}
	
	public void getViewPoint(Vec3 output) {
		output.set(0, 0, 0);
	}

}
