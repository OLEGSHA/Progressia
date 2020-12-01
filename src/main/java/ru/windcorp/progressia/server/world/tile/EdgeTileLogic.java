package ru.windcorp.progressia.server.world.tile;

import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.block.BlockTickContext;

public class EdgeTileLogic extends TileLogic implements UpdateableTile {

	public EdgeTileLogic(String id) {
		super(id);
	}
	
	@Override
	public void update(TileTickContext context) {
		if (!canOccupyFace(context)) {
			context.removeThisTile();
		}
	}
	
	@Override
	public boolean canOccupyFace(TileTickContext context) {
		boolean canOccupyCurrent = canOccupyFace(context, context.getCurrentFace(), context.getCurrentBlockContext());
		boolean canOccupyCounter = canOccupyFace(context, context.getCounterFace(), context.getCounterBlockContext());
		
		return (canOccupyCurrent != canOccupyCounter) || (canOccupyCurrent && canOccupyCounter && canBeSquashed(context));
	}
	
	public boolean canOccupyFace(TileTickContext ownContext, BlockFace blockFace, BlockTickContext blockContext) {
		BlockLogic block = blockContext.getBlock();
		if (block == null) return false;
		
		return block.isSolid(blockContext, ownContext.getCurrentFace());
	}
	
	public boolean canBeSquashed(TileTickContext context) {
		return false;
	}

}
