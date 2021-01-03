package ru.windcorp.progressia.common.world.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.PacketAffectWorld;
import ru.windcorp.progressia.common.world.WorldData;

public class PacketAffectEntity extends PacketAffectWorld {
	
	private long entityId;

	public PacketAffectEntity(String id) {
		super(id);
	}
	
	public long getEntityId() {
		return entityId;
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

	@Override
	public void apply(WorldData world) {
		world.removeEntity(this.entityId);
	}

}
