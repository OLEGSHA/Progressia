package ru.windcorp.progressia.client.comms;

import java.io.IOException;
import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.graphics.world.EntityAnchor;
import ru.windcorp.progressia.client.world.ChunkRender;
import ru.windcorp.progressia.common.comms.CommsListener;
import ru.windcorp.progressia.common.comms.packets.Packet;
import ru.windcorp.progressia.common.comms.packets.PacketSetLocalPlayer;
import ru.windcorp.progressia.common.comms.packets.PacketWorldChange;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.entity.PacketEntityChange;

// TODO refactor with no mercy
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
			
			if (!(packet instanceof PacketEntityChange)) {
				tmp_reassembleWorld();
			}
		} else if (packet instanceof PacketSetLocalPlayer) {
			setLocalPlayer((PacketSetLocalPlayer) packet);
		}
	}

	private void setLocalPlayer(PacketSetLocalPlayer packet) {
		EntityData entity = getClient().getWorld().getData().getEntity(
				packet.getLocalPlayerEntityId()
		);
		
		if (entity == null) {
			CrashReports.report(null, "Player entity not found");
		}

		getClient().setLocalPlayer(entity);
		getClient().getCamera().setAnchor(new EntityAnchor(
				getClient().getWorld().getEntityRenderable(entity)
		));
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
