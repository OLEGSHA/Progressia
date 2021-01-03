package ru.windcorp.progressia.common.world.entity;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import ru.windcorp.progressia.common.state.IOContext;
import ru.windcorp.progressia.common.util.DataBuffer;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;

public class PacketChangeEntity extends PacketAffectEntity {

	private final DataBuffer buffer = new DataBuffer();

	public PacketChangeEntity() {
		super("Core:EntityChange");
	}
	
	protected PacketChangeEntity(String id) {
		super(id);
	}
	
	public DataBuffer getBuffer() {
		return buffer;
	}
	
	public void set(EntityData entity) {
		super.set(entity.getEntityId());
		
		try {
			entity.write(this.buffer.getWriter(), IOContext.COMMS);
		} catch (IOException e) {
			throw CrashReports.report(e, "Entity could not be written");
		}
	}
	
	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		super.read(input);
		this.buffer.fill(input, input.readInt());
	}
	
	@Override
	public void write(DataOutput output) throws IOException {
		super.write(output);
		output.writeInt(this.buffer.getSize());
		this.buffer.flush(output);
	}

	@Override
	public void apply(WorldData world) {
		EntityData entity = world.getEntity(getEntityId());

		if (entity == null) {
			throw CrashReports.report(null, "Entity with ID %d not found", getEntityId());
		}

		try {
			entity.read(getBuffer().getReader(), IOContext.COMMS);
		} catch (IOException e) {
			throw CrashReports.report(e, "Entity could not be read");
		}
	}

}
