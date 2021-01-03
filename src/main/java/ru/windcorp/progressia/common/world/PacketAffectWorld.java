package ru.windcorp.progressia.common.world;

import ru.windcorp.progressia.common.comms.packets.Packet;

public abstract class PacketAffectWorld extends Packet {

	public PacketAffectWorld(String id) {
		super(id);
	}
	
	public abstract void apply(WorldData world);

}
