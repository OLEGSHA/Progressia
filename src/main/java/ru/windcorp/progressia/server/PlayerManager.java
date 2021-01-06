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
		player.setDirection(new Vec2(
				Math.toRadians(40), Math.toRadians(10)
		));
		
		getServer().getWorld().getData().addEntity(player);
		
		return player;
	}

	public Server getServer() {
		return server;
	}

}
