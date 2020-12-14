package ru.windcorp.progressia.server.world.tile;

import ru.windcorp.progressia.server.world.block.BlockLogic;

public class HangingTileLogic extends TileLogic implements UpdateableTile {

	public HangingTileLogic(String id) {
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
		BlockLogic host = context.getBlock();
		if (host == null) return false;
		
		if (!host.isSolid(context, context.getFace())) return false;
		
		if (canBeSquashed(context)) return true;
		
		return context.evalComplementary(ctxt -> {
			BlockLogic complHost = ctxt.getBlock();
			return complHost == null || !complHost.isSolid(ctxt, context.getFace());
		});
	}
	
	public boolean canBeSquashed(TileTickContext context) {
		return false;
	}

}
