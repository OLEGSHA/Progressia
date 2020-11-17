package ru.windcorp.progressia.client.comms.controls;

import java.util.function.Predicate;

import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.client.graphics.input.KeyEvent;
import ru.windcorp.progressia.common.comms.controls.ControlDataRegistry;
import ru.windcorp.progressia.common.comms.controls.PacketControl;
import ru.windcorp.progressia.common.util.namespaces.NamespacedUtil;

public class ControlTriggerOnKeyPress extends ControlTriggerInputBased {
	
	private final Predicate<KeyEvent> predicate;
	private final PacketControl packet;

	public ControlTriggerOnKeyPress(
			String id,
			Predicate<KeyEvent> predicate
	) {
		super(id);
		this.predicate = predicate;
		this.packet = new PacketControl(
				NamespacedUtil.getId(getNamespace(), "ControlKeyPress" + getName()),
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
