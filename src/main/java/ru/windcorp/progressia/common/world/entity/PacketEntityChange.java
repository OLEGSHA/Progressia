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

public class PacketEntityChange extends PacketWorldChange {

	private long entityId;
	private final DataBuffer buffer = new DataBuffer();

	public PacketEntityChange() {
		super("Core:EntityChange");
	}
	
	protected PacketEntityChange(String id) {
		super(id);
	}

	public long getEntityId() {
		return entityId;
	}

	public void setEntityId(long entityId) {
		this.entityId = entityId;
	}

	public DataBuffer getBuffer() {
		return buffer;
	}

	public DataInput getReader() {
		return buffer.getReader();
	}

	public DataOutput getWriter() {
		return buffer.getWriter();
	}
	
	public void set(EntityData entity) {
		this.entityId = entity.getEntityId();
		try {
			entity.write(this.buffer.getWriter(), IOContext.COMMS);
		} catch (IOException e) {
			CrashReports.report(e, "Entity could not be written");
		}
	}
	
	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		this.entityId = input.readLong();
		this.buffer.fill(input, input.readInt());
	}
	
	@Override
	public void write(DataOutput output) throws IOException {
		output.writeLong(this.entityId);
		output.writeInt(this.buffer.getSize());
		this.buffer.flush(output);
	}

	@Override
	public void apply(WorldData world) {
		EntityData entity = world.getEntity(getEntityId());

		if (entity == null) {
			CrashReports.report(null, "Entity with ID %d not found", getEntityId());
		}

		try {
			entity.read(getReader(), IOContext.COMMS);
		} catch (IOException e) {
			CrashReports.report(e, "Entity could not be read");
		}
	}

}
