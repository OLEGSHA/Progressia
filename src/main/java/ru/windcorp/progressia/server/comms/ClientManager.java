/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.windcorp.progressia.server.comms;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import glm.vec._3.i.Vec3i;
import gnu.trove.TCollections;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import ru.windcorp.progressia.common.comms.CommsChannel.State;
import ru.windcorp.progressia.common.comms.packets.Packet;
import ru.windcorp.progressia.common.world.PacketSetLocalPlayer;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.Player;
import ru.windcorp.progressia.server.Server;

public class ClientManager {

	private final Server server;

	private final TIntObjectMap<Client> clientsById = TCollections.synchronizedMap(new TIntObjectHashMap<>());

	private final Collection<Client> clients = Collections.unmodifiableCollection(clientsById.valueCollection());

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

				if (client instanceof ClientPlayer) {
					addClientPlayer((ClientPlayer) client);
				}
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

		PacketSetLocalPlayer packet = new PacketSetLocalPlayer();
		packet.set(entity.getEntityId());
		client.sendPacket(packet);
	}

	public void disconnectClient(Client client) {
		client.disconnect();
		clientsById.remove(client.getId());
	}

	/**
	 * Sends the provided packet to all connected player clients.
	 * 
	 * @param packet
	 *            the packet to broadcast
	 */
	public void broadcastToAllPlayers(Packet packet) {
		getClients().forEach(c -> {
			if (c.getState() != State.CONNECTED)
				return;
			if (!(c instanceof ClientPlayer))
				return;
			c.sendPacket(packet);
		});
	}

	/**
	 * Sends the provided packet to all connected player clients that can see
	 * the chunk identified by {@code chunkPos}.
	 * 
	 * @param packet
	 *            the packet to broadcast
	 * @param chunkPos
	 *            the chunk coordinates of the chunk that must be visible
	 */
	public void broadcastLocal(Packet packet, Vec3i chunkPos) {
		getClients().forEach(c -> {
			if (c.getState() != State.CONNECTED)
				return;
			if (!(c instanceof ClientPlayer))
				return;
			if (!((ClientPlayer) c).isChunkVisible(chunkPos))
				return;
			c.sendPacket(packet);
		});
	}

	/**
	 * Sends the provided packet to all connected player clients that can see
	 * the entity identified by {@code entityId}.
	 * 
	 * @param packet
	 *            the packet to broadcast
	 * @param entityId
	 *            the ID of the entity that must be visible
	 */
	public void broadcastLocal(Packet packet, long entityId) {
		getClients().forEach(c -> {
			if (c.getState() != State.CONNECTED)
				return;
			if (!(c instanceof ClientPlayer))
				return;
			if (!((ClientPlayer) c).isChunkVisible(entityId))
				return;
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
