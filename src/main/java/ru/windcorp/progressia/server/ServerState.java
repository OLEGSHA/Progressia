package ru.windcorp.progressia.server;

import ru.windcorp.progressia.common.world.WorldData;

public class ServerState {
	
	private static Server instance = null;
	
	public static Server getInstance() {
		return instance;
	}
	
	public static void setInstance(Server instance) {
		ServerState.instance = instance;
	}
	
	public static void startServer() {
		Server server = new Server(new WorldData());
		server.getWorld().getData().tmp_generate();
		setInstance(server);
		server.start();
	}
	
	private ServerState() {}

}
