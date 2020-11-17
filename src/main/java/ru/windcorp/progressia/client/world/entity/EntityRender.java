package ru.windcorp.progressia.client.world.entity;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.entity.EntityData;

public abstract class EntityRender extends Namespaced {

	public EntityRender(String id) {
		super(id);
	}
	
	public abstract EntityRenderable createRenderable(EntityData entity);

}
