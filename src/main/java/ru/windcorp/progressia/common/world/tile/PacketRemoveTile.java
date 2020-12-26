package ru.windcorp.progressia.common.world.tile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.PacketChunkChange;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class PacketRemoveTile extends PacketChunkChange {
	
	private final Vec3i blockInWorld = new Vec3i();
	private BlockFace face;
	private int tag;
	
	public PacketRemoveTile() {
		this("Core:RemoveTile");
	}
	
	protected PacketRemoveTile(String id) {
		super(id);
	}
	
	public void set(Vec3i blockInWorld, BlockFace face, int tag) {
		this.blockInWorld.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
		this.face = face;
		this.tag = tag;
	}
	
	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		this.blockInWorld.set(input.readInt(), input.readInt(), input.readInt());
		this.face = BlockFace.getFaces().get(input.readByte());
		this.tag = input.readInt();
	}
	
	@Override
	public void write(DataOutput output) throws IOException {
		output.writeInt(this.blockInWorld.x);
		output.writeInt(this.blockInWorld.y);
		output.writeInt(this.blockInWorld.z);
		output.writeByte(this.face.getId());
		output.writeInt(this.tag);
	}

	@Override
	public void apply(WorldData world) {
		TileDataStack stack = world.getTiles(blockInWorld, face);
		stack.remove(stack.getIndexByTag(tag));
	}
	
	@Override
	public void getAffectedChunk(Vec3i output) {
		Coordinates.convertInWorldToChunk(this.blockInWorld, output);
	}

}
