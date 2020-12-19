package ru.windcorp.progressia.common.comms.packets;

import java.io.IOException;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.io.ChunkIO;
import ru.windcorp.progressia.common.util.DataBuffer;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;

public class PacketLoadChunk extends PacketWorldChange {
	
	private final DataBuffer data = new DataBuffer();
	private final Vec3i position = new Vec3i();

	public PacketLoadChunk(String id) {
		super(id);
	}

	@Override
	public void apply(WorldData world) {
		try {
			world.addChunk(ChunkIO.load(world, position, data.getInputStream()));
		} catch (DecodingException | IOException e) {
			CrashReports.report(e, "Could not load chunk");
		}
	}
	
	public Vec3i getPosition() {
		return position;
	}
	
	public DataBuffer getData() {
		return data;
	}

}
