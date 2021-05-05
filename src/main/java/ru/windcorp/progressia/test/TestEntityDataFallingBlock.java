package ru.windcorp.progressia.test;

import ru.windcorp.progressia.common.collision.AABB;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;

/**
 * Data for Test:FallingBlock
 * @author opfromthestart
 *
 */
public class TestEntityDataFallingBlock extends EntityData {
	
	private BlockData block;

	public TestEntityDataFallingBlock() {
		this("Test:FallingBlock",new BlockData("Test:Sand"));
	}
	
	protected TestEntityDataFallingBlock(String id, BlockData blockInput) {
		super(id);
		setCollisionModel(new AABB(0,0,0,1,1,1));
		block = blockInput;
	}
	
	public BlockData getBlock()
	{
		return block;
	}
}
