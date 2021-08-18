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
import ru.windcorp.progressia.common.world.entity.PacketChangeEntity;
import ru.windcorp.progressia.server.Server;

class ChangeEntity extends CachedChange {

	private EntityData entity;
	private StateChange<?> change;

	private final PacketChangeEntity packet = new PacketChangeEntity();

	public ChangeEntity(Consumer<? super CachedChange> disposer) {
		super(disposer);
	}

	public <T extends EntityData> void set(T entity, StateChange<T> change) {
		if (this.entity != null)
			throw new IllegalStateException("Entity is not null. Current: " + this.entity + "; requested: " + entity);

		if (this.change != null)
			throw new IllegalStateException("Change is not null. Current: " + this.change + "; requested: " + change);

		this.entity = entity;
		this.change = change;

	}

	@SuppressWarnings("unchecked")
	@Override
	public void affect(Server server) {
		((StateChange<EntityData>) change).change(entity);
		packet.set(entity);

		server.getClientManager().broadcastLocal(packet, entity.getEntityId());
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
		this.entity = null;
		this.change = null;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(entity);
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof ChangeEntity))
			return false;
		return ((ChangeEntity) obj).entity == entity;
	}

}
