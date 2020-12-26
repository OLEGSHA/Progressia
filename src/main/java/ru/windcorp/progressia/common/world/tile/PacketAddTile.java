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

public class PacketAddTile extends PacketChunkChange {
	
	private String id;
	private final Vec3i blockInWorld = new Vec3i();
	private BlockFace face;
	
	public PacketAddTile() {
		this("Core:AddTile");
	}
	
	protected PacketAddTile(String id) {
		super(id);
	}
	
	public void set(TileData tile, Vec3i blockInWorld, BlockFace face) {
		this.id = tile.getId();
		this.blockInWorld.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
		this.face = face;
	}
	
	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		this.id = input.readUTF();
		this.blockInWorld.set(input.readInt(), input.readInt(), input.readInt());
		this.face = BlockFace.getFaces().get(input.readByte());
	}
	
	@Override
	public void write(DataOutput output) throws IOException {
		output.writeUTF(this.id);
		output.writeInt(this.blockInWorld.x);
		output.writeInt(this.blockInWorld.y);
		output.writeInt(this.blockInWorld.z);
		output.writeByte(this.face.getId());
	}

	@Override
	public void apply(WorldData world) {
		TileData tile = TileDataRegistry.getInstance().get(id);
		world.getTiles(blockInWorld, face).add(tile);
	}
	
	@Override
	public void getAffectedChunk(Vec3i output) {
		Coordinates.convertInWorldToChunk(this.blockInWorld, output);
	}

}
