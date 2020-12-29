package ru.windcorp.progressia.common.world.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ru.windcorp.progressia.common.world.PacketWorldChange;
import ru.windcorp.progressia.common.world.WorldData;

public class PacketRevokeEntity extends PacketWorldChange {
	
	private long entityId;
	
	public PacketRevokeEntity() {
		this("Core:RevokeEntity");
	}

	protected PacketRevokeEntity(String id) {
		super(id);
	}
	
	public void set(long entityId) {
		this.entityId = entityId;
	}
	
	@Override
	public void read(DataInput input) throws IOException {
		this.entityId = input.readLong();
	}
	
	@Override
	public void write(DataOutput output) throws IOException {
		output.writeLong(this.entityId);
	}

	@Override
	public void apply(WorldData world) {
		world.removeEntity(this.entityId);
	}

}
