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
 
package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.Server;

class RemoveEntity extends CachedChange {

	private long entityId = EntityData.NULL_ENTITY_ID;

	public RemoveEntity(Consumer<? super CachedChange> disposer) {
		super(disposer);
	}

	public void set(long entityId) {
		if (this.entityId != EntityData.NULL_ENTITY_ID)
			throw new IllegalStateException("Entity ID is not null. Current: " + this.entityId + "; requested: " + entityId);

		this.entityId = entityId;
	}

	@Override
	public void affect(Server server) {
		server.getWorld().getData().removeEntity(entityId);
	}

	@Override
	public void getRelevantChunk(Vec3i output) {
		// Do nothing
	}

	@Override
	public boolean isThreadSensitive() {
		return false;
	}

	@Override
	public void dispose() {
		super.dispose();
		this.entityId = EntityData.NULL_ENTITY_ID;
	}

	@Override
	public int hashCode() {
		return Long.hashCode(entityId);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof RemoveEntity))
			return false;
		return ((RemoveEntity) obj).entityId == entityId;
	}

}
