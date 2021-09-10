package ru.windcorp.progressia.test;

import ru.windcorp.progressia.common.collision.AABB;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.entity.EntityData;

/**
 * Data for Test:FallingBlock
 * 
 * @author opfromthestart
 *
 */
public class TestEntityDataFallingBlock extends EntityData {

	private BlockData block;
	private boolean isDone = false;
	private boolean hasDeleted = false;

	public TestEntityDataFallingBlock()
	{
		this("Test:FallingBlock");
	}
	
	public TestEntityDataFallingBlock(String id, BlockData inBlock) {
		super(id);
		block = inBlock;
		setCollisionModel(new AABB(0, 0, 0, 1, 1, 1));
	}

	public TestEntityDataFallingBlock(String string) {
		this(string, new BlockData((String) TestEntityLogicFallingBlock.FallingBlocks.toArray()[0]));
	}

	public TestEntityDataFallingBlock(BlockData current) {
		this("Test:FallingBlock"+current.getId().substring(5), current);
	}

	public void setDestroyed() {
		hasDeleted = true;
	}

	public boolean hasDestroyed() {
		return hasDeleted;
	}
	
	public BlockData getBlock()
	{
		return block;
	}

	public void setInvisible() {
		// block = new BlockData("Test:Log");
		isDone = true;
		setCollisionModel(new AABB(0, 0, 0, 0.5f, 0.5f, 0.5f));
	}

	public boolean isDone() {
		return isDone;
	}
}
