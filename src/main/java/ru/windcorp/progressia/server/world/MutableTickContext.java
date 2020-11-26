package ru.windcorp.progressia.server.world;

import ru.windcorp.progressia.server.Server;

public class MutableTickContext implements TickContext {

	private double tickLength;
	private Server server;
	private WorldLogic world;

	public MutableTickContext() {
		super();
	}

	public double getTickLength() {
		return tickLength;
	}

	public void setTickLength(double tickLength) {
		this.tickLength = tickLength;
	}

	@Override
	public Server getServer() {
		return server;
	}

	public void setServer(Server server) {
		this.server = server;
		this.setTickLength(server.getTickLength());
		setWorld(server.getWorld());
	}

	@Override
	public WorldLogic getWorld() {
		return world;
	}

	public void setWorld(WorldLogic world) {
		this.world = world;
	}

}