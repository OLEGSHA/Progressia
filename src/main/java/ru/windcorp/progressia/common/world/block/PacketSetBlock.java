package ru.windcorp.progressia.common.world.block;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.PacketChunkChange;
import ru.windcorp.progressia.common.world.WorldData;

public class PacketSetBlock extends PacketChunkChange {
	
	private String id;
	private final Vec3i blockInWorld = new Vec3i();
	
	public PacketSetBlock() {
		this("Core:SetBlock");
	}
	
	protected PacketSetBlock(String id) {
		super(id);
	}
	
	public void set(BlockData block, Vec3i blockInWorld) {
		this.id = block.getId();
		this.blockInWorld.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
	}

	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		this.id = input.readUTF();
		this.blockInWorld.set(input.readInt(), input.readInt(), input.readInt());
	}

	@Override
	public void write(DataOutput output) throws IOException {
		output.writeUTF(this.id);
		output.writeInt(this.blockInWorld.x);
		output.writeInt(this.blockInWorld.y);
		output.writeInt(this.blockInWorld.z);
	}

	@Override
	public void apply(WorldData world) {
		BlockData block = BlockDataRegistry.getInstance().get(id);
		world.setBlock(blockInWorld, block, true);
	}

	@Override
	public void getAffectedChunk(Vec3i output) {
		Coordinates.convertInWorldToChunk(this.blockInWorld, output);
	}

}
