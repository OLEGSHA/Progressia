package ru.windcorp.progressia.common.world;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ru.windcorp.progressia.common.comms.packets.Packet;

public class PacketSetLocalPlayer extends Packet {
	
	private long entityId;

	public PacketSetLocalPlayer() {
		this("Core:SetLocalPlayer");
	}
	
	protected PacketSetLocalPlayer(String id) {
		super(id);
	}
	
	public void set(long entityId) {
		this.entityId = entityId;
	}
	
	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		this.entityId = input.readLong();
	}
	
	@Override
	public void write(DataOutput output) throws IOException {
		output.writeLong(this.entityId);
	}
	
	public long getEntityId() {
		return entityId;
	}

}
