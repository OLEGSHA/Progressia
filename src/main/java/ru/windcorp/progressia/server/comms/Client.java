package ru.windcorp.progressia.server.comms;

import ru.windcorp.progressia.common.comms.CommsChannel;

public abstract class Client extends CommsChannel {
	
	private final int id;
	
	public Client(int id, Role... roles) {
		super(roles);
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName() + " " + id;
	}

}
