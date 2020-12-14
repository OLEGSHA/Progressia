package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.TickAndUpdateUtil;
import ru.windcorp.progressia.server.world.WorldLogic;

class BlockTriggeredUpdate extends CachedEvaluation {
	
	private final Vec3i blockInWorld = new Vec3i();

	public BlockTriggeredUpdate(Consumer<? super CachedEvaluation> disposer) {
		super(disposer);
	}

	@Override
	public void evaluate(Server server) {
		Vec3i cursor = new Vec3i(blockInWorld.x, blockInWorld.y, blockInWorld.z);
		
		WorldLogic world = server.getWorld();
		
		for (BlockFace face : BlockFace.getFaces()) {
			TickAndUpdateUtil.updateTiles(world, cursor, face);
			cursor.add(face.getVector());
			TickAndUpdateUtil.updateBlock(world, cursor);
			TickAndUpdateUtil.updateTiles(world, cursor, face.getCounter());
			cursor.sub(face.getVector());
		}
	}
	
	public void init(Vec3i blockInWorld) {
		this.blockInWorld.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
	}

	@Override
	public void getRelevantChunk(Vec3i output) {
		Coordinates.convertInWorldToChunk(blockInWorld, output);
	}

}
