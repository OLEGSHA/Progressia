package ru.windcorp.progressia.common.world;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.io.ChunkIO;
import ru.windcorp.progressia.common.util.DataBuffer;
import ru.windcorp.progressia.common.util.crash.CrashReports;

public class PacketSendChunk extends PacketChunkChange {
	
	private final DataBuffer data = new DataBuffer();
	private final Vec3i position = new Vec3i();
	
	public PacketSendChunk() {
		this("Core:SendChunk");
	}

	protected PacketSendChunk(String id) {
		super(id);
	}
	
	public void set(ChunkData chunk) {
		this.position.set(chunk.getX(), chunk.getY(), chunk.getZ());
		
		try {
			ChunkIO.save(chunk, this.data.getOutputStream());
		} catch (IOException e) {
			// Impossible
		}
	}
	
	@Override
	public void read(DataInput input) throws IOException {
		this.position.set(input.readInt(), input.readInt(), input.readInt());
		this.data.fill(input, input.readInt());
	}
	
	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.position.x);
		output.writeInt(this.position.y);
		output.writeInt(this.position.z);
		output.writeInt(this.data.getSize());
		this.data.flush(output);
	}

	@Override
	public void apply(WorldData world) {
		try {
			world.addChunk(ChunkIO.load(world, position, data.getInputStream()));
		} catch (DecodingException | IOException e) {
			CrashReports.report(e, "Could not load chunk");
		}
	}
	
	@Override
	public void getAffectedChunk(Vec3i output) {
		output.set(getPosition().x, getPosition().y, getPosition().z);
	}
	
	public Vec3i getPosition() {
		return position;
	}
	
	public DataBuffer getData() {
		return data;
	}

}
