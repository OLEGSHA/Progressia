package ru.windcorp.progressia.server.world.tile;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.generic.GenericTile;

public class TileLogic extends Namespaced implements GenericTile {

	public TileLogic(String id) {
		super(id);
	}
	
	public boolean canOccupyFace(TileTickContext context) {
		return canOccupyFace(context.getFace());
	}
	
	public boolean canOccupyFace(BlockFace face) {
		return true;
	}
	
	public boolean isSolid(TileTickContext context) {
		return isSolid();
	}
	
	public boolean isSolid() {
		return false;
	}

}
