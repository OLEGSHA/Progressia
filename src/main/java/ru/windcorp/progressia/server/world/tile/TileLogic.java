package ru.windcorp.progressia.server.world.tile;

import ru.windcorp.progressia.common.util.Namespaced;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class TileLogic extends Namespaced {

	public TileLogic(String namespace, String name) {
		super(namespace, name);
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
