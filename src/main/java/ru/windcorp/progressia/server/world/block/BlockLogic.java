package ru.windcorp.progressia.server.world.block;

import ru.windcorp.progressia.common.util.namespaces.Namespaced;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.generic.GenericBlock;

public class BlockLogic extends Namespaced implements GenericBlock {

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
