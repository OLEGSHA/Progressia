package ru.windcorp.progressia.client.comms;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.graphics.world.EntityAnchor;
import ru.windcorp.progressia.client.world.ChunkRender;
import ru.windcorp.progressia.common.comms.CommsListener;
import ru.windcorp.progressia.common.comms.packets.Packet;
import ru.windcorp.progressia.common.comms.packets.PacketSetLocalPlayer;
import ru.windcorp.progressia.common.comms.packets.PacketWorldChange;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.entity.EntityData;

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
		} else if (packet instanceof PacketSetLocalPlayer) {
			setLocalPlayer((PacketSetLocalPlayer) packet);
		}
	}

	private void setLocalPlayer(PacketSetLocalPlayer packet) {
		UUID uuid = packet.getLocalPlayerEntityUUID();
		
		Collection<ChunkData> chunks =
				getClient().getWorld().getData().getChunks();
		
		EntityData entity = null;
		
		synchronized (chunks) {
			chunkLoop:
			for (ChunkData chunk : chunks) {
				for (EntityData anEntity : chunk.getEntities()) {
					if (anEntity.getUUID().equals(uuid)) {
						entity = anEntity;
						break chunkLoop;
					}
				}
			}
		}
		
		if (entity == null) {
			throw new RuntimeException("");
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
