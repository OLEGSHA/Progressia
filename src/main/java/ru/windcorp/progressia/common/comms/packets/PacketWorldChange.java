package ru.windcorp.progressia.common.comms.packets;

import ru.windcorp.progressia.common.world.WorldData;

public abstract class PacketWorldChange extends Packet {

	public PacketWorldChange(String id) {
		super(id);
	}
	
	public abstract void apply(WorldData world);

}
