package ru.windcorp.progressia.common.world.tile;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.crash.CrashReports;
import ru.windcorp.progressia.common.world.DecodingException;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class PacketRemoveTile extends PacketAffectTile {
	
	public PacketRemoveTile() {
		this("Core:RemoveTile");
	}
	
	protected PacketRemoveTile(String id) {
		super(id);
	}
	
	@Override
	public void set(Vec3i blockInWorld, BlockFace face, int tag) {
		super.set(blockInWorld, face, tag);
	}
	
	@Override
	public void read(DataInput input) throws IOException, DecodingException {
		super.read(input);
	}
	
	@Override
	public void write(DataOutput output) throws IOException {
		super.write(output);
	}

	@Override
	public void apply(WorldData world) {
		TileDataStack stack = world.getTiles(getBlockInWorld(), getFace());
		
		int index = stack.getIndexByTag(getTag());
		
		if (index < 0) {
			throw CrashReports.report(null,
					"Could not find tile with tag %d at (%d; %d; %d; %s)",
					getTag(),
					getBlockInWorld().x, getBlockInWorld().y, getBlockInWorld().z,
					getFace()
			);
		}
		
		stack.remove(index);
	}

}
