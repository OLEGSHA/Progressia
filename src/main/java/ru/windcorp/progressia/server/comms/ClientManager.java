package ru.windcorp.progressia.server.comms;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import ru.windcorp.progressia.common.comms.CommsChannel.Role;
import ru.windcorp.progressia.common.comms.CommsChannel.State;
import ru.windcorp.progressia.common.comms.packets.Packet;
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
		clientsById.put(client.getId(), client);
		
		client.addListener(new DefaultServerCommsListener(this, client));
	}
	
	public void disconnectClient(Client client) {
		client.disconnect();
		clientsById.remove(client.getId());
	}
	
	public void broadcastGamePacket(Packet packet) {
		getClients().forEach(c -> {
				if (c.getState() != State.CONNECTED) return;
				if (!c.getRoles().contains(Role.GAME)) return;
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
