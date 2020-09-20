package ru.windcorp.progressia.client.world.entity;

import ru.windcorp.progressia.common.util.Namespaced;
import ru.windcorp.progressia.common.world.entity.EntityData;

public abstract class EntityRender extends Namespaced {

	public EntityRender(String namespace, String name) {
		super(namespace, name);
	}
	
	public abstract EntityRenderable createRenderable(EntityData entity);

}
