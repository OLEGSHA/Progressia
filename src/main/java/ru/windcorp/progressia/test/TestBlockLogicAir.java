package ru.windcorp.progressia.test;

import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.server.world.block.BlockLogic;

public class TestBlockLogicAir extends BlockLogic {
	
	public TestBlockLogicAir(String id) {
		super(id);
	}
	
	@Override
	public boolean isSolid(BlockFace face) {
		return false;
	}
	
	@Override
	public boolean isTransparent() {
		return true;
	}

}
