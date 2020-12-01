package ru.windcorp.progressia.test;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.TickAndUpdateUtil;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.BlockTickContext;
import ru.windcorp.progressia.server.world.ticking.TickingPolicy;
import ru.windcorp.progressia.server.world.tile.EdgeTileLogic;
import ru.windcorp.progressia.server.world.tile.TickableTile;
import ru.windcorp.progressia.server.world.tile.TileTickContext;

public class TestTileLogicGrass extends EdgeTileLogic implements TickableTile {

	public TestTileLogicGrass(String id) {
		super(id);
	}
	
	@Override
	public TickingPolicy getTickingPolicy(TileTickContext context) {
		return TickingPolicy.RANDOM;
	}
	
	@Override
	public void tick(TileTickContext context) {
		if (!isLocationSuitable(context)) {
			context.removeThisTile();
		}
	}

	@Override
	public boolean canBeSquashed(TileTickContext context) {
		return true;
	}

	private boolean isLocationSuitable(TileTickContext context) {
		return
				isSuitableHost(context.getCurrentBlockContext(), context.getCurrentFace()) !=
				isSuitableHost(context.getCounterBlockContext(), context.getCounterFace());
	}

	private boolean isSuitableHost(BlockTickContext bctxt, BlockFace face) {
		if (face == BlockFace.BOTTOM) return false;
		
		BlockLogic block = bctxt.getBlock();
		if (block == null) return false;
		if (!block.isSolid(bctxt, face)) return false;
		return isBlockAboveTransparent(bctxt.getServer(), bctxt.getBlockInWorld());
	}
	
	private boolean isBlockAboveTransparent(Server server, Vec3i blockInWorld) {
		BlockTickContext bctxt = TickAndUpdateUtil.getBlockTickContext(server, blockInWorld.add_(BlockFace.TOP.getVector()));
		
		BlockLogic block = bctxt.getBlock();
		if (block == null) return true;
		
		return block.isTransparent(bctxt);
	}

}
