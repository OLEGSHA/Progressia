package ru.windcorp.progressia.client.world.entity;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.common.world.entity.EntityData;

public abstract class EntityRenderable implements Renderable {
	
	private final EntityData data;
	
	public EntityRenderable(EntityData data) {
		this.data = data;
	}

	public EntityData getData() {
		return data;
	}
	
	public void getViewPoint(Vec3 output) {
		output.set(0, 0, 0);
	}

}
