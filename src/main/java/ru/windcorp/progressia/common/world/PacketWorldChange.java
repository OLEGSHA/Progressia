package ru.windcorp.progressia.common.world;

import ru.windcorp.progressia.common.comms.packets.Packet;

public abstract class PacketWorldChange extends Packet {

	public PacketWorldChange(String id) {
		super(id);
	}
	
	public abstract void apply(WorldData world);

}
