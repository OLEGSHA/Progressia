package ru.windcorp.progressia.server.comms.controls;

import ru.windcorp.progressia.common.comms.controls.PacketControl;
import ru.windcorp.progressia.common.util.Namespaced;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.comms.Client;

public abstract class ControlLogic extends Namespaced {

	public ControlLogic(String namespace, String name) {
		super(namespace, name);
	}
	
	public abstract void apply(
			Server server,
			PacketControl packet,
			Client client
	);

}
