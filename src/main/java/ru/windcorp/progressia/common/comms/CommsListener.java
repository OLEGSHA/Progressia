package ru.windcorp.progressia.common.comms;

import java.io.IOException;

import ru.windcorp.progressia.common.comms.packets.Packet;

public interface CommsListener {
	
	void onPacketReceived(Packet packet);
	
	void onIOError(IOException reason);

}
