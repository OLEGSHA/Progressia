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

package ru.windcorp.progressia.client.comms.localhost;

import ru.windcorp.progressia.client.comms.ServerCommsChannel;
import ru.windcorp.progressia.common.comms.packets.Packet;
import ru.windcorp.progressia.server.Server;

public class LocalServerCommsChannel extends ServerCommsChannel {

	private LocalClient localClient;
	private final Server server;

	public LocalServerCommsChannel(Server server) {
		this.server = server;
	}

	public void connect(String login) {
		setState(State.CONNECTED);

		this.localClient = new LocalClient(server.getClientManager().grabClientId(), login, this);

		server.getClientManager().addClient(localClient);
	}

	@Override
	protected void doSendPacket(Packet packet) {
		localClient.relayPacketToServer(packet);
	}

	public void relayPacketToClient(Packet packet) {
		onPacketReceived(packet);
	}

	@Override
	public void disconnect() {
		// Do nothing
	}

}
