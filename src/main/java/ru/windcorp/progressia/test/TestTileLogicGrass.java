package ru.windcorp.progressia.test;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.TickAndUpdateUtil;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.BlockTickContext;
import ru.windcorp.progressia.server.world.tile.EdgeTileLogic;
import ru.windcorp.progressia.server.world.tile.TileTickContext;

public class TestTileLogicGrass extends EdgeTileLogic {

	public TestTileLogicGrass(String id) {
		super(id);
	}
	
	private boolean isBlockAboveTransparent(Server server, Vec3i blockInWorld) {
		Vec3i blockAboveCoords = Vectors.grab3i();
		blockAboveCoords.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
		blockAboveCoords.add(BlockFace.TOP.getVector());
		
		BlockTickContext blockAboveContext = TickAndUpdateUtil.grabBlockTickContext(server, blockAboveCoords);
		
		try {
			BlockLogic blockAbove = blockAboveContext.getBlock();
			if (blockAbove == null) return true;
			
			return blockAbove.isTransparent(blockAboveContext);
		} finally {
			TickAndUpdateUtil.releaseTickContext(blockAboveContext);
			Vectors.release(blockAboveCoords);
		}
	}
	
	@Override
	public void update(TileTickContext context) {
		super.update(context);
		
		if (
				!(
						context.getCurrentFace() == BlockFace.BOTTOM
						||
						isBlockAboveTransparent(context.getServer(), context.getCurrentBlockInWorld())
				)
				||
				!(
						context.getCounterFace() == BlockFace.BOTTOM
						||
						isBlockAboveTransparent(context.getServer(), context.getCounterBlockInWorld())
				)
		) {
			context.removeThisTile();
		}
	}

}
