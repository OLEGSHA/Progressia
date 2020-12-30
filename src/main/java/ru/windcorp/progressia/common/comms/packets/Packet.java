package ru.windcorp.progressia.common.comms.packets;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.DecodingException;

public abstract class Packet extends Namespaced {

	public Packet(String id) {
		super(id);
	}

	public abstract void read(DataInput input) throws IOException, DecodingException;
	public abstract void write(DataOutput output) throws IOException;
	
}
