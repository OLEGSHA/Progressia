package ru.windcorp.progressia.common.world.tile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class PacketAddTile extends PacketAffectTile {
	
	private String tileId;
	
	public PacketAddTile() {
		this("Core:AddTile");
	}
	
	protected PacketAddTile(String id) {
		super(id);
	}
	
	public String getTileId() {
		return tileId;
	}
	
	public void set(TileData tile, Vec3i blockInWorld, BlockFace face) {
		super.set(blockInWorld, face, -1);
		this.tileId = tile.getId();
	}
	
	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		super.read(input);
		this.tileId = input.readUTF();
	}
	
	@Override
	public void write(DataOutput output) throws IOException {
		super.write(output);
		output.writeUTF(this.tileId);
	}

	@Override
	public void apply(WorldData world) {
		TileData tile = TileDataRegistry.getInstance().get(getTileId());
		world.getTiles(getBlockInWorld(), getFace()).add(tile);
	}

}
