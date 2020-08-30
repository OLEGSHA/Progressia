package ru.windcorp.progressia.common.comms.controls;

import ru.windcorp.progressia.common.comms.packets.Packet;

public class PacketControl extends Packet {
	
	private final ControlData control;

	public PacketControl(String namespace, String name, ControlData control) {
		super(namespace, name);
		this.control = control;
	}
	
	public ControlData getControl() {
		return control;
	}

}
