package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.TickAndUpdateUtil;
import ru.windcorp.progressia.server.world.WorldLogic;

class TileTriggeredUpdate extends CachedEvaluation {
	
	private final Vec3i blockInWorld = new Vec3i();
	private BlockFace face = null;

	public TileTriggeredUpdate(Consumer<? super CachedEvaluation> disposer) {
		super(disposer);
	}

	@Override
	public void evaluate(Server server) {
		Vec3i cursor = new Vec3i(blockInWorld.x, blockInWorld.y, blockInWorld.z);
		
		WorldLogic world = server.getWorld();
		
		TickAndUpdateUtil.tickTiles(world, cursor, face); // Tick facemates (also self)
		TickAndUpdateUtil.tickBlock(world, cursor); // Tick block on one side
		cursor.add(face.getVector());
		TickAndUpdateUtil.tickBlock(world, cursor); // Tick block on the other side
	}
	
	public void init(Vec3i blockInWorld, BlockFace face) {
		this.blockInWorld.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
		this.face = face;
	}

	@Override
	public void getRelevantChunk(Vec3i output) {
		Coordinates.convertInWorldToChunk(blockInWorld, output);
	}

}
