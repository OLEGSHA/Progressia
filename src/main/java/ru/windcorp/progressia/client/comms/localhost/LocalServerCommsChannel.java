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
		
		this.localClient = new LocalClient(
				server.getClientManager().grabClientId(),
				login,
				this
		);
		
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
