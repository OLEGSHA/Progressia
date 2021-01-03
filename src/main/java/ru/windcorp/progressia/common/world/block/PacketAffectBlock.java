package ru.windcorp.progressia.common.world.block;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.PacketAffectChunk;

public abstract class PacketAffectBlock extends PacketAffectChunk {
	
	private final Vec3i blockInWorld = new Vec3i();
	
	public PacketAffectBlock(String id) {
		super(id);
	}
	
	public Vec3i getBlockInWorld() {
		return blockInWorld;
	}
	
	public void set(Vec3i blockInWorld) {
		this.blockInWorld.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
	}

	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		this.blockInWorld.set(input.readInt(), input.readInt(), input.readInt());
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.blockInWorld.x);
		output.writeInt(this.blockInWorld.y);
		output.writeInt(this.blockInWorld.z);
	}

	@Override
	public void getAffectedChunk(Vec3i output) {
		Coordinates.convertInWorldToChunk(this.blockInWorld, output);
	}

}
