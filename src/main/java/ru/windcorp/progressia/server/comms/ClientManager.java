package ru.windcorp.progressia.server.comms;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import ru.windcorp.progressia.common.comms.CommsChannel.State;
import ru.windcorp.progressia.common.comms.packets.Packet;
import ru.windcorp.progressia.common.comms.packets.PacketLoadChunk;
import ru.windcorp.progressia.common.comms.packets.PacketSetLocalPlayer;
import ru.windcorp.progressia.common.io.ChunkIO;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.Player;
import ru.windcorp.progressia.server.Server;

public class ClientManager {
	
	private final Server server;
	
	private final TIntObjectMap<Client> clientsById =
			TCollections.synchronizedMap(new TIntObjectHashMap<>());
	
	private final Collection<Client> clients =
			Collections.unmodifiableCollection(clientsById.valueCollection());
	
	private final AtomicInteger nextId = new AtomicInteger(0);

	public ClientManager(Server server) {
		this.server = server;
	}
	
	public int grabClientId() {
		return nextId.getAndIncrement();
	}
	
	public void addClient(Client client) {
		synchronized (client) {
			clientsById.put(client.getId(), client);
			
			if (client instanceof ClientChat) {
				addClientChat((ClientChat) client);
			}
			
			if (client instanceof ClientPlayer) {
				addClientPlayer((ClientPlayer) client);
			}
			
			client.addListener(new DefaultServerCommsListener(this, client));
		}
	}
	
	private void addClientChat(ClientChat client) {
		// Do nothing
	}

	private void addClientPlayer(ClientPlayer client) {
		String login = client.getLogin();
		EntityData entity = getServer().getPlayerManager().conjurePlayerEntity(login);
		
		Player player = new Player(entity, getServer(), client);
		
		getServer().getPlayerManager().getPlayers().add(player);
		
		for (ChunkData chunk : server.getWorld().getData().getChunks()) {
			PacketLoadChunk packet = new PacketLoadChunk("Core:LoadChunk");
			packet.getPosition().set(
					chunk.getPosition().x,
					chunk.getPosition().y,
					chunk.getPosition().z
			);
			
			try {
				ChunkIO.save(chunk, packet.getData().getOutputStream());
			} catch (IOException e) {
				CrashReports.report(e, "ClientManager fjcked up. javahorse stupid");
			}
			client.sendPacket(packet);
		}

		client.sendPacket(new PacketSetLocalPlayer(entity.getEntityId()));
	}

	public void disconnectClient(Client client) {
		client.disconnect();
		clientsById.remove(client.getId());
	}
	
	public void broadcastGamePacket(Packet packet) {
		getClients().forEach(c -> {
				if (c.getState() != State.CONNECTED) return;
				if (!(c instanceof ClientPlayer)) return;
				c.sendPacket(packet);
		});
	}
	
	public Collection<Client> getClients() {
		return clients;
	}
	
	public Client getById(int id) {
		return clientsById.get(id);
	}
	
	public Server getServer() {
		return server;
	}

}
