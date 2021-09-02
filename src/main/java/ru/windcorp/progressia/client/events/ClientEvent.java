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

/**
 * An interface for all events issued by a {@link Client}.
 */
public interface ClientEvent {

	/**
	 * Returns the client instance that this event happened on.
	 * 
	 * @return the client
	 */
	Client getClient();

	/**
	 * Sets the client instance that the event is posted on. The value provided
	 * to this method must be returned by subsequent calls to
	 * {@link #getClient()}. Do not call this method when handling the event.
	 * 
	 * @param client the client dispatching the event or {@code null} to unbind
	 *               any previously bound client
	 */
	void setClient(Client client);

	/**
	 * A default implementation of {@link ClientEvent}. This is not necessarily
	 * extended by client events.
	 */
	public static abstract class Default implements ClientEvent {

		private Client client;

		public Default(Client client) {
			this.client = client;
		}

		@Override
		public Client getClient() {
			return client;
		}

		@Override
		public void setClient(Client client) {
			this.client = client;
		}

	}

}
