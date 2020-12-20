package ru.windcorp.progressia.server;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.entity.EntityData;
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
			// TODO load appropriate chunks
			return getServer().getWorld().getData().getEntity(TestContent.PLAYER_ENTITY_ID);
		} else {
			CrashReports.report(null, "Unknown login %s, javahorse stupid", login);
			return null;
		}
	}
	
	public Server getServer() {
		return server;
	}

}
