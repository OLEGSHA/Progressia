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
				
				ControlLogicRegistry.getInstance().get(
						packetControl.getControl().getId()
				).apply(manager.getServer(), packetControl, client);
			}
		}
	}

	@Override
	public void onIOError(IOException reason) {
		// TODO Auto-generated method stub

	}

}
