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
			((PacketAffectWorld) packet).apply(
					getClient().getWorld().getData()
			);
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
