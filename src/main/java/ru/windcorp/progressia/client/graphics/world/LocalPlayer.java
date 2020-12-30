package ru.windcorp.progressia.client.graphics.world;

import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.world.WorldRender;
import ru.windcorp.progressia.client.world.entity.EntityRenderable;
import ru.windcorp.progressia.common.world.entity.EntityData;

public class LocalPlayer {

	private final Client client;
	
	private long entityId = EntityData.NULL_ENTITY_ID;
	private EntityData lastKnownEntity = null;
	
	private final Selection selection = new Selection();
	
	public LocalPlayer(Client client) {
		this.client = client;
	}
	
	public Client getClient() {
		return client;
	}
	
	public long getEntityId() {
		return entityId;
	}
	
	public void setEntityId(long entityId) {
		this.entityId = entityId;
	
		this.lastKnownEntity = null;
		getEntity();
	}
	
	public boolean hasEntityId() {
		return entityId != EntityData.NULL_ENTITY_ID;
	}
	
	public boolean hasEntity() {
		return getEntity() != null;
	}
	
	public EntityData getEntity() {
		if (!hasEntityId()) {
			return null;
		}
		
		EntityData entity = getClient().getWorld().getData().getEntity(getEntityId());
		
		if (entity != lastKnownEntity) {
			getClient().onLocalPlayerEntityChanged(entity, lastKnownEntity);
			this.lastKnownEntity = entity;
		}
		
		return entity;
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
