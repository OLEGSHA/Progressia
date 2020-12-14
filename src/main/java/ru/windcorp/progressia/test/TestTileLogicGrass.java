package ru.windcorp.progressia.test;

import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.BlockTickContext;
import ru.windcorp.progressia.server.world.ticking.TickingPolicy;
import ru.windcorp.progressia.server.world.tile.HangingTileLogic;
import ru.windcorp.progressia.server.world.tile.TickableTile;
import ru.windcorp.progressia.server.world.tile.TileTickContext;

public class TestTileLogicGrass extends HangingTileLogic implements TickableTile {

	public TestTileLogicGrass(String id) {
		super(id);
	}
	
	@Override
	public boolean canOccupyFace(TileTickContext context) {
		return context.getFace() != BlockFace.BOTTOM && super.canOccupyFace(context);
	}
	
	@Override
	public boolean canOccupyFace(BlockFace face) {
		return face != BlockFace.BOTTOM;
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
		return canOccupyFace(context) && isBlockAboveTransparent(context);
	}
	
	private boolean isBlockAboveTransparent(BlockTickContext context) {
		return context.evalNeighbor(BlockFace.TOP, bctxt -> {
			BlockLogic block = bctxt.getBlock();
			if (block == null) return true;
			
			return block.isTransparent(bctxt);
		});
	}

}
