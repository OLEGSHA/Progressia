package ru.windcorp.progressia.test;

import org.apache.logging.log4j.LogManager;

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

	public TestEntityDataFallingBlock() {
		this("Test:FallingBlock", new BlockData("Test:LogTop"));
	}
	
	public TestEntityDataFallingBlock(BlockData data) {
		this("Test:FallingBlock", data);
	}

	protected TestEntityDataFallingBlock(String id, BlockData blockInput) {
		super(id);
		setCollisionModel(new AABB(0, 0, 0, 1, 1, 1));
		block = blockInput;
		LogManager.getLogger().info(blockInput.getId());
	}

	public void setDestroyed() {
		hasDeleted = true;
	}

	public boolean hasDestroyed() {
		return hasDeleted;
	}

	public BlockData getBlock() {
		return block;
	}

	public void setInvisible() {
		// block = new BlockData("Test:Log");
		isDone = true;
		setCollisionModel(new AABB(0, 0, 0, .5f, 0.5f, 0.5f));
	}

	public boolean isDone() {
		return isDone;
	}
}
