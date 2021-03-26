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

import ru.windcorp.progressia.server.Server;

/**
 * An interface for all events issued by a {@link Server}.
 */
public interface ServerEvent {

	/**
	 * Returns the server instance that this event happened on.
	 * 
	 * @return the relevant server
	 */
	Server getServer();

	/**
	 * Sets the server instance that the event is posted on. The value provided
	 * to this method must be returned by subsequent calls to
	 * {@link #getServer()}. Do not call this method when handling the event.
	 * 
	 * @param server the server dispatching the event or {@code null} to unbind
	 *               any previously bound server
	 */
	void setServer(Server server);

	/**
	 * A default implementation of {@link ServerEvent}. This is not necessarily
	 * extended by server events.
	 */
	public static abstract class Default implements ServerEvent {

		private Server server;

		public Default(Server server) {
			this.server = server;
		}

		@Override
		public Server getServer() {
			return server;
		}

		@Override
		public void setServer(Server server) {
			this.server = server;
		}

	}

}
