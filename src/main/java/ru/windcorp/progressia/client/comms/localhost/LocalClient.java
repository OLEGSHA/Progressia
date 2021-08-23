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

import java.io.IOException;

import ru.windcorp.progressia.common.comms.packets.Packet;
import ru.windcorp.progressia.server.comms.ClientPlayer;

public class LocalClient extends ClientPlayer {

	private final LocalServerCommsChannel serverComms;

	private final String login;

	public LocalClient(int id, String login, LocalServerCommsChannel serverComms) {
		super(id);
		setState(State.CONNECTED);

		this.serverComms = serverComms;
		this.login = login;
	}

	@Override
	public String getLogin() {
		return this.login;
	}

	@Override
	protected void doSendPacket(Packet packet) throws IOException {
		this.serverComms.relayPacketToClient(packet);
	}

	public void relayPacketToServer(Packet packet) {
		onPacketReceived(packet);
	}

	@Override
	public void disconnect() {
		// Do nothing
	}

}
