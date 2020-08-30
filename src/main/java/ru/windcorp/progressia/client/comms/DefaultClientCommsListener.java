package ru.windcorp.progressia.client.comms;

import java.io.IOException;

import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.world.ChunkRender;
import ru.windcorp.progressia.common.comms.CommsListener;
import ru.windcorp.progressia.common.comms.packets.Packet;
import ru.windcorp.progressia.common.comms.packets.PacketWorldChange;

public class DefaultClientCommsListener implements CommsListener {
	
	private final Client client;

	public DefaultClientCommsListener(Client client) {
		this.client = client;
	}

	@Override
	public void onPacketReceived(Packet packet) {
		if (packet instanceof PacketWorldChange) {
			((PacketWorldChange) packet).apply(
					getClient().getWorld().getData()
			);
			
			tmp_reassembleWorld();
		}
	}

	private void tmp_reassembleWorld() {
		getClient().getWorld().getChunks().forEach(ChunkRender::markForUpdate);
	}

	@Override
	public void onIOError(IOException reason) {
		// TODO implement
	}
	
	public Client getClient() {
		return client;
	}

}
