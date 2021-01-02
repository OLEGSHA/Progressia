package ru.windcorp.progressia.common.world.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.util.DataBuffer;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.PacketWorldChange;
import ru.windcorp.progressia.common.world.WorldData;

public class PacketSendEntity extends PacketWorldChange {
	
	private String id;
	private long entityId;
	private final DataBuffer buffer = new DataBuffer();
	
	public PacketSendEntity() {
		this("Core:SendEntity");
	}
	
	protected PacketSendEntity(String id) {
		super(id);
	}
	
	public void set(EntityData entity) {
		this.id = entity.getId();
		this.entityId = entity.getEntityId();
		
		try {
			entity.write(this.buffer.getWriter(), IOContext.COMMS);
		} catch (IOException e) {
			throw CrashReports.report(e, "Could not write an entity into an internal buffer");
		}
	}

	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		this.id = input.readUTF();
		this.entityId = input.readLong();
		this.buffer.fill(input, input.readInt());
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeUTF(this.id);
		output.writeLong(this.entityId);
		output.writeInt(this.buffer.getSize());
		this.buffer.flush(output);
	}
	
	@Override
	public void apply(WorldData world) {
		EntityData entity = EntityDataRegistry.getInstance().create(this.id);
		
		entity.setEntityId(this.entityId);
		try {
			entity.read(this.buffer.getReader(), IOContext.COMMS);
		} catch (IOException e) {
			throw CrashReports.report(e, "Could not read an entity from an internal buffer");
		}
		
		world.addEntity(entity);
	}

}
