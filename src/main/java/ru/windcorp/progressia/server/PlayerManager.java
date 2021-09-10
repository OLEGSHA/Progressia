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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.entity.EntityDataPlayer;
import ru.windcorp.progressia.common.world.entity.EntityDataRegistry;
import ru.windcorp.progressia.common.world.item.ItemDataRegistry;
import ru.windcorp.progressia.server.events.PlayerJoinedEvent;
import ru.windcorp.progressia.test.TestContent;

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

	public EntityData conjurePlayerEntity(String login) {
		// TODO Live up to the name
		if (TestContent.PLAYER_LOGIN.equals(login)) {
			EntityData entity = spawnPlayerEntity(login);
			return entity;
		} else {
			throw CrashReports.report(null, "Unknown login %s, javahorse stupid", login);
		}
	}

	private EntityDataPlayer spawnPlayerEntity(String login) {
		EntityDataPlayer player = (EntityDataPlayer) EntityDataRegistry.getInstance().create("Core:Player");

		player.getHand(0).slot().setContents(ItemDataRegistry.getInstance().create("Test:Stick"), 7);
		player.getEquipmentSlot(0).slot().setContents(ItemDataRegistry.getInstance().create("Test:CardboardBackpack"), 1);

		player.setPosition(getServer().getWorld().getGenerator().suggestSpawnLocation());
		player.setUpVector(new Vec3(0, 0, 1));
		player.setLookingAt(new Vec3(2, 1, 0));
		
		getServer().getWorld().spawnEntity(player);

		return player;
	}
	
	public Object getMutex() {
		return players;
	}

	public Server getServer() {
		return server;
	}

}
