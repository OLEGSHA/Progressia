package ru.windcorp.progressia.client.comms.controls;

import java.util.function.BiConsumer;
import java.util.function.Predicate;

import ru.windcorp.progressia.client.graphics.input.InputEvent;
import ru.windcorp.progressia.common.comms.controls.ControlData;
import ru.windcorp.progressia.common.comms.controls.ControlDataRegistry;
import ru.windcorp.progressia.common.comms.controls.PacketControl;
import ru.windcorp.progressia.common.util.namespaces.NamespacedUtil;

public class ControlTriggerLambda extends ControlTriggerInputBased {
	
	private final String packetId;
	private final Predicate<InputEvent> predicate;
	private final BiConsumer<InputEvent, ControlData> dataWriter;

	public ControlTriggerLambda(
			String id,
			Predicate<InputEvent> predicate,
			BiConsumer<InputEvent, ControlData> dataWriter
	) {
		super(id);
		
		this.packetId = NamespacedUtil.getId(
				NamespacedUtil.getNamespace(id),
				"ControlKeyPress" + NamespacedUtil.getName(id)
		);
		
		this.predicate = predicate;
		this.dataWriter = dataWriter;
	}

	@Override
	public PacketControl onInputEvent(InputEvent event) {
		if (!predicate.test(event)) return null;
		
		PacketControl packet = new PacketControl(
				packetId,
				ControlDataRegistry.getInstance().create(getId())
		);
		
		dataWriter.accept(event, packet.getControl());
		
		return packet;
	}

}
