package ru.windcorp.progressia.server.comms.controls;

import ru.windcorp.progressia.common.comms.controls.PacketControl;
import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.comms.Client;

public abstract class ControlLogic extends Namespaced {
	
	@FunctionalInterface
	public static interface Lambda {
		void apply(
				Server server,
				PacketControl packet,
				Client client
		);
	}

	public ControlLogic(String id) {
		super(id);
	}
	
	public abstract void apply(
			Server server,
			PacketControl packet,
			Client client
	);
	
	public static ControlLogic of(String id, Lambda logic) {
		return new ControlLogic(id) {
			@Override
			public void apply(Server server, PacketControl packet, Client client) {
				logic.apply(server, packet, client);
			}
		};
	}

}
