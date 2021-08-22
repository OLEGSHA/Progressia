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
package ru.windcorp.progressia.server.events;

import ru.windcorp.progressia.server.Player;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.comms.Client;

public interface PlayerEvent extends ClientEvent {
	
	Player getPlayer();
	
	public static abstract class Immutable extends ServerEvent.Default implements PlayerEvent {
		
		private final Player player;

		public Immutable(Server server, Player player) {
			super(server);
			this.player = player;
		}
		
		@Override
		public Player getPlayer() {
			return player;
		}
		
		@Override
		public Client getClient() {
			return player.getClient();
		}
		
	}

}
