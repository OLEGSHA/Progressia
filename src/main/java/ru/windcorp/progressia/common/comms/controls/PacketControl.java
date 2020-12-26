package ru.windcorp.progressia.common.comms.controls;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ru.windcorp.progressia.common.comms.packets.Packet;
import ru.windcorp.progressia.common.world.DecodingException;

public class PacketControl extends Packet {
	
	private final ControlData control;

	public PacketControl(String id, ControlData control) {
		super(id);
		this.control = control;
	}
	
	public ControlData getControl() {
		return control;
	}

	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		// TODO implement controls
	}

	@Override
	public void write(DataOutput output) throws IOException {
		// implement controls
	}

}
