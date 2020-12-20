package ru.windcorp.progressia.client.graphics.world;

import ru.windcorp.progressia.client.world.WorldRender;
import ru.windcorp.progressia.client.world.entity.EntityRenderable;
import ru.windcorp.progressia.common.world.PlayerData;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class LocalPlayer extends PlayerData {

	private final Selection selection = new Selection();
	
	public LocalPlayer(EntityData entity) {
		super(entity);
	}
	
	public Selection getSelection() {
		return selection;
	}
	
	public void update(WorldRender world) {
		getSelection().update(world, getEntity());
	}

	public EntityRenderable getRenderable(WorldRender world) {
		return world.getEntityRenderable(getEntity());
	}

}
