package ru.windcorp.progressia.test;

import glm.vec._3.i.Vec3i;
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
		BlockTickContext blockAboveContext = TickAndUpdateUtil.getBlockTickContext(server, blockInWorld.add_(BlockFace.TOP.getVector()));
		
		BlockLogic blockAbove = blockAboveContext.getBlock();
		if (blockAbove == null) return true;
		
		return blockAbove.isTransparent(blockAboveContext);
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
