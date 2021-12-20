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

package ru.windcorp.progressia.server;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;
import ru.windcorp.progressia.common.world.entity.EntityDataRegistry;
import ru.windcorp.progressia.common.world.item.ItemDataRegistry;
import ru.windcorp.progressia.common.world.item.inventory.Items;
import ru.windcorp.progressia.server.comms.ClientPlayer;
import ru.windcorp.progressia.server.events.PlayerJoinedEvent;
import ru.windcorp.progressia.server.events.PlayerLeftEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class PlayerManager {

	private final Server server;

	private final Collection<Player> players = Collections.synchronizedCollection(new ArrayList<>());

	public PlayerManager(Server server) {
		this.server = server;
	}

	public Collection<Player> getPlayers() {
		return players;
	}

	public void addPlayer(Player player) {
		this.players.add(player);
		getServer().postEvent(new PlayerJoinedEvent.Immutable(getServer(), player));
	}

	public void removePlayer(Player player) {
		server.getWorld().getContainer().savePlayer(player, server);
		this.players.remove(player);
		getServer().postEvent(new PlayerLeftEvent.Immutable(getServer(), player));
	}

	public Player conjurePlayer(ClientPlayer clientPlayer, String login) {

		Player player = getServer().getWorld().getContainer().loadPlayer(login, clientPlayer, getServer());
		if (player == null) { // create new player
			EntityData entity = spawnPlayerEntity(login);
			player = new Player(entity, getServer(), clientPlayer);
		}

		getServer().getWorld().spawnEntity(player.getEntity());

		return player;
	}

	private EntityDataPlayer spawnPlayerEntity(String login) {
		EntityDataPlayer player = (EntityDataPlayer) EntityDataRegistry.getInstance().create("Core:Player");

		Items.spawn(player.getHand(0).slot(), ItemDataRegistry.getInstance().create("Test:Stick"), 5);
		Items.spawn(player.getEquipmentSlot(0).slot(), ItemDataRegistry.getInstance().create("Test:CardboardBackpack"), 1);

		player.setPosition(getServer().getWorld().getGenerator().suggestSpawnLocation());
		player.setUpVector(new Vec3(0, 0, 1));
		player.setLookingAt(new Vec3(2, 1, 0));

		return player;
	}

	public Object getMutex() {
		return players;
	}

	public Server getServer() {
		return server;
	}

}
