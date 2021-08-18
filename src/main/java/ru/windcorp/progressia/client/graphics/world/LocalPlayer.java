/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

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
