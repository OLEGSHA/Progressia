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
