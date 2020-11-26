package ru.windcorp.progressia.server.world.block;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class BlockLogic extends Namespaced {

	public BlockLogic(String id) {
		super(id);
	}
	
	public boolean isSolid(BlockTickContext context, BlockFace face) {
		return isSolid(face);
	}
	
	public boolean isSolid(BlockFace face) {
		return true;
	}
	
	public boolean isTransparent(BlockTickContext context) {
		return isTransparent();
	}

	public boolean isTransparent() {
		return false;
	}

}
