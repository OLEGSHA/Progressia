package ru.windcorp.progressia.client.comms.controls;

import ru.windcorp.progressia.client.Client;
import ru.windcorp.progressia.client.graphics.input.bus.Input;
import ru.windcorp.progressia.common.comms.packets.Packet;

public class InputBasedControls {
	
	private final Client client;
	
	private final ControlTriggerInputBased[] controls;

	public InputBasedControls(Client client) {
		this.client = client;
		
		this.controls = ControlTriggerRegistry.getInstance().values().stream()
				.filter(ControlTriggerInputBased.class::isInstance)
				.toArray(ControlTriggerInputBased[]::new);
	}

	public void handleInput(Input input) {
		for (ControlTriggerInputBased c : controls) {
			Packet packet = c.onInputEvent(input.getEvent());
			
			if (packet != null) {
				input.consume();
				client.getComms().sendPacket(packet);
				break;
			}
		}
	}

}
