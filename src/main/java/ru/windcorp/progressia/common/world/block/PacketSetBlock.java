package ru.windcorp.progressia.common.world.block;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;

public class PacketSetBlock extends PacketAffectBlock {
	
	private String blockId;
	
	public PacketSetBlock() {
		this("Core:SetBlock");
	}
	
	protected PacketSetBlock(String id) {
		super(id);
	}
	
	public String getBlockId() {
		return blockId;
	}
	
	public void set(BlockData block, Vec3i blockInWorld) {
		super.set(blockInWorld);
		this.blockId = block.getId();
	}

	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		super.read(input);
		this.blockId = input.readUTF();
	}

	@Override
	public void write(DataOutput output) throws IOException {
		super.write(output);
		output.writeUTF(this.blockId);
	}

	@Override
	public void apply(WorldData world) {
		BlockData block = BlockDataRegistry.getInstance().get(getBlockId());
		world.setBlock(getBlockInWorld(), block, true);
	}

}
