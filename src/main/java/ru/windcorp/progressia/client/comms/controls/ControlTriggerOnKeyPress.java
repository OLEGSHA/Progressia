package ru.windcorp.progressia.client.comms.controls;

import java.util.function.Predicate;

import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.common.comms.controls.ControlDataRegistry;
import ru.windcorp.progressia.common.comms.controls.PacketControl;

public class ControlTriggerOnKeyPress extends ControlTriggerInputBased {
	
	private final Predicate<KeyEvent> predicate;
	private final PacketControl packet;

	public ControlTriggerOnKeyPress(
			String namespace, String name,
			Predicate<KeyEvent> predicate
	) {
		super(namespace, name);
		this.predicate = predicate;
		this.packet = new PacketControl(
				getNamespace(), "ControlKeyPress" + getName(),
				ControlDataRegistry.getInstance().get(getId())
		);
	}

	@Override
	public PacketControl onInputEvent(InputEvent event) {
		if (!(event instanceof KeyEvent)) return null;
		
		KeyEvent keyEvent = (KeyEvent) event;
		
		if (!keyEvent.isPress()) return null;
		if (!predicate.test(keyEvent)) return null;
		
		return packet;
	}

}
