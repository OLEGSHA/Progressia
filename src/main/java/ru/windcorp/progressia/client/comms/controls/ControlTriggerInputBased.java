package ru.windcorp.progressia.client.comms.controls;

import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.common.comms.controls.PacketControl;

public abstract class ControlTriggerInputBased extends ControlTrigger {

	public ControlTriggerInputBased(String id) {
		super(id);
	}
	
	public abstract PacketControl onInputEvent(InputEvent event);

}
