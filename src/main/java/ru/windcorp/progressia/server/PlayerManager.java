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

import glm.vec._2.Vec2;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.entity.EntityDataRegistry;
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

	private EntityData spawnPlayerEntity(String login) {
		EntityData player = EntityDataRegistry.getInstance().create("Test:Player");

		player.setEntityId(TestContent.PLAYER_ENTITY_ID);
		player.setPosition(TestContent.SPAWN);
		player.setDirection(new Vec2(Math.toRadians(40), Math.toRadians(10)));

		getServer().getWorld().getData().addEntity(player);

		return player;
	}

	public Server getServer() {
		return server;
	}

}
