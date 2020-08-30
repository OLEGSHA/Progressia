package ru.windcorp.progressia.client.comms.localhost;

import java.io.IOException;

import ru.windcorp.progressia.common.comms.packets.Packet;
import ru.windcorp.progressia.server.comms.Client;

public class LocalClient extends Client {
	
	private final LocalServerCommsChannel serverComms;

	public LocalClient(int id, LocalServerCommsChannel serverComms) {
		super(id, Role.GAME, Role.CHAT);
		setState(State.CONNECTED);
		
		this.serverComms = serverComms;
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
