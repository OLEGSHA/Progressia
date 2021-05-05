package ru.windcorp.progressia.test;

import org.apache.logging.log4j.LogManager;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.world.TickContext;
import ru.windcorp.progressia.server.world.entity.EntityLogic;

/**
 * Logic for Test:FallingBlock
 * @author opfromthestart
 *
 */
public class TestEntityLogicFallingBlock extends EntityLogic {

	public TestEntityLogicFallingBlock(String id) {
		super(id);
	}

	@Override
	public void tick(EntityData entity, TickContext context) {
		super.tick(entity, context);
		
		//friction
		Vec3 vel = entity.getVelocity();
		float friction = .8f;
		vel = new Vec3(vel.x*friction,vel.y*friction,vel.z);
		entity.setVelocity(vel);

		
		TestEntityDataFallingBlock fallBlock = (TestEntityDataFallingBlock) context.getServer().getWorld().getData().getEntity(entity.getEntityId());;

		Vec3i occupiedBlock = entity.getBlockInWorld(null);
		Vec3i underBlock = occupiedBlock.sub_(0, 0, 1);
		
		Vec3i chunkCoords = underBlock.div_(16);
		Vec3i inChunkCoords = underBlock.mod_(new Vec3i(16));
		
		LogManager.getLogger().info("FallingBlock is at "+String.valueOf(occupiedBlock.x)+" "+String.valueOf(occupiedBlock.y)+" "+String.valueOf(occupiedBlock.z));
		LogManager.getLogger().info("Block is of type " + context.getWorldData().getChunk(chunkCoords).getBlock(inChunkCoords).getId());

		if (context.getServer().getWorld().getData().getChunk(chunkCoords).getBlock(inChunkCoords)
				.getId() != "Test:Air") {
			LogManager.getLogger().info("Deleting FallingBlock at " + String.valueOf(occupiedBlock.x));
			context.getServer().getWorldAccessor().setBlock(occupiedBlock, fallBlock.getBlock());
			context.getServer().getWorld().getData().removeEntity(entity);
		}
	}
}
