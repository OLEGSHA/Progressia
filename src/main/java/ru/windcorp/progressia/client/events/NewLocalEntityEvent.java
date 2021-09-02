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
package ru.windcorp.progressia.client.events;

import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;

public interface NewLocalEntityEvent extends ClientEvent {

	EntityDataPlayer getNewEntity();
	EntityDataPlayer getPreviousEntity();
	
	public class Immutable extends ClientEvent.Default implements NewLocalEntityEvent {
		
		private final EntityDataPlayer newEntity;
		private final EntityDataPlayer previousEntity;
		
		public Immutable(Client client, EntityDataPlayer newEntity, EntityDataPlayer previousEntity) {
			super(client);
			this.newEntity = newEntity;
			this.previousEntity = previousEntity;
		}
		
		@Override
		public EntityDataPlayer getNewEntity() {
			return newEntity;
		}
		
		@Override
		public EntityDataPlayer getPreviousEntity() {
			return previousEntity;
		}
		
	}

}
