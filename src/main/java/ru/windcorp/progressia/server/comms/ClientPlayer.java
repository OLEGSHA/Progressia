package ru.windcorp.progressia.server.comms;

public abstract class ClientPlayer extends Client {

	public ClientPlayer(int id) {
		super(id);
	}
	
	public abstract String getLogin();

}
