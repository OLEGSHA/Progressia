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

package ru.windcorp.progressia.client.comms;

import java.io.IOException;

import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.common.comms.CommsListener;
import ru.windcorp.progressia.common.comms.packets.Packet;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.PacketSetLocalPlayer;
import ru.windcorp.progressia.common.world.PacketAffectWorld;

// TODO refactor with no mercy
public class DefaultClientCommsListener implements CommsListener {

	private final Client client;

	public DefaultClientCommsListener(Client client) {
		this.client = client;
	}

	@Override
	public void onPacketReceived(Packet packet) {
		if (packet instanceof PacketAffectWorld) {
			((PacketAffectWorld) packet).apply(getClient().getWorld().getData());
		} else if (packet instanceof PacketSetLocalPlayer) {
			setLocalPlayer((PacketSetLocalPlayer) packet);
		}
	}

	private void setLocalPlayer(PacketSetLocalPlayer packet) {
		getClient().getLocalPlayer().setEntityId(packet.getEntityId());
	}

	@Override
	public void onIOError(IOException reason) {
		throw CrashReports.report(reason, "An IOException has occurred in communications");
		// TODO implement
	}

	public Client getClient() {
		return client;
	}

}
