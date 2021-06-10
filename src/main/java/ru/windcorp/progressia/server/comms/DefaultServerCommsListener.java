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

package ru.windcorp.progressia.server.comms;

import java.io.IOException;

import ru.windcorp.progressia.common.comms.controls.PacketControl;
import ru.windcorp.progressia.common.comms.CommsListener;
import ru.windcorp.progressia.common.comms.packets.Packet;
import ru.windcorp.progressia.server.comms.controls.ControlLogicRegistry;

public class DefaultServerCommsListener implements CommsListener {

	private final ClientManager manager;
	private final Client client;

	public DefaultServerCommsListener(ClientManager manager, Client client) {
		this.manager = manager;
		this.client = client;
	}

	@Override
	public void onPacketReceived(Packet packet) {
		if (client instanceof ClientPlayer) {
			if (packet instanceof PacketControl) {
				PacketControl packetControl = (PacketControl) packet;

				ControlLogicRegistry.getInstance().get(packetControl.getControl().getId()).apply(manager.getServer(),
						packetControl, client);
			}
		}
	}

	@Override
	public void onIOError(IOException reason) {
		// TODO Auto-generated method stub

	}

}
