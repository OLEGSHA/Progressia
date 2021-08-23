package ru.windcorp.progressia.test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.world.context.ServerWorldContext;
import ru.windcorp.progressia.server.world.entity.EntityLogic;
import ru.windcorp.progressia.test.Rocks.Rock;
import ru.windcorp.progressia.test.Rocks.RockVariant;

/**
 * Logic for Test:FallingBlock
 * 
 * @author opfromthestart
 *
 */
public class TestEntityLogicFallingBlock extends EntityLogic {

	public static Set<String> FallingBlocks = new HashSet<String>();

	public void addFallables() {
		FallingBlocks.add("Test:Sand");
		for (Rock rock : TestContent.ROCKS.getRocks())
		{
			FallingBlocks.add(rock.getBlock(RockVariant.GRAVEL).getId());
			FallingBlocks.add(rock.getBlock(RockVariant.SAND).getId());
		}
	}

	public TestEntityLogicFallingBlock(String id) {
		super(id);
		addFallables();
	}

	/*private Vec3i trueMod(Vec3i input, Vec3i modulus) // Move this to a class in
														// Vec or something
	{
		return input.mod_(modulus).add_(modulus).mod_(modulus);
	}

	private Vec3i trueDiv(Vec3i input, Vec3i divisor) // Move this to a class in
														// Vec or something
	{
		Vec3i temp = input.div_(divisor);
		temp.add(new Vec3i(input.x < 0 ? -1 : 0, input.y < 0 ? -1 : 0, input.z < 0 ? -1 : 0));
		return temp;
	}*/
	
	public Vec3i getBestCardinal(Vec3 dir)
	{
		Vec3 a = dir.abs_();
		if (a.x>a.y && a.x>a.z)
		{
			return new Vec3i(dir.x>0 ? 1 : -1,0,0);
		}
		else if (a.y>a.z)
		{
			return new Vec3i(0,dir.y>0 ? 1 : -1,0);
		}
		return new Vec3i(0,0,dir.z>0 ? 1 : -1);
	}
	
	public List<Vec3i> getGoodCardinals(Vec3 dir)
	{
		return getGoodCardinals(dir,.05f);
	}

	public List<Vec3i> getGoodCardinals(Vec3 dir, float d) {
		List<Vec3i> list = new ArrayList<>();
		Vec3 a = dir.abs_();
		if (a.x>d)
		{
			list.add(new Vec3i(dir.x>0 ? 1 : -1,0,0));
		}
		if (a.y>d)
		{
			list.add(new Vec3i(0,dir.y>0 ? 1 : -1,0));
		}
		if (a.z>d)
		{
			list.add(new Vec3i(0,0,dir.z>0 ? 1 : -1));
		}
		return list;
	}

	@Override
	public void tick(EntityData entity, ServerWorldContext context) { // context.getWorldData()
																// ClientState.getInstance().getWorld().getData()
		
		if (entity == null) {
			return;
		}
		

		// LogManager.getLogger().info("NotNull "+entity.toString()+"
		// "+String.valueOf(entity!=null) + " " +
		// context.toString());
		super.tick(entity, context);
		
		
		// friction
		Vec3 vel = entity.getVelocity();
		float friction = 0f;
		vel = new Vec3(vel.x * friction, vel.y * friction, vel.z);
		entity.setVelocity(vel);

		//TestEntityDataFallingBlock fallBlock = (TestEntityDataFallingBlock) context.//context.getEntity(entity.getEntityId());
		
		TestEntityDataFallingBlock fallBlock = (TestEntityDataFallingBlock) ClientState.getInstance().getWorld()
				.getData().getEntity(entity.getEntityId()); 
		TestEntityDataFallingBlock fallBlock2 = (TestEntityDataFallingBlock) entity;// ClientState.getInstance().getWorld().getData().getEntity(entity.getEntityId());
		// fallBlock = (TestEntityDataFallingBlock) entity;

		// LogManager.getLogger().info("NotNull FB
		// "+String.valueOf(fallBlock!=null));
		if (fallBlock == null) {
			return;
		}
		
		

		if (fallBlock.isDone() || context.getBlock(fallBlock.getBlockInWorld(null)) == null) {
			return;
		}
		
		//LogManager.getLogger().info("wut");

		if (!fallBlock.hasDestroyed()) {
			LogManager.getLogger().info(fallBlock.getPosition().x);
			context.setBlock(fallBlock.getBlockInWorld(null),
					BlockDataRegistry.getInstance().get("Test:Air"));
			fallBlock.setDestroyed();
		}

		Vec3i occupiedBlock = fallBlock.getBlockInWorld(null);
		Vec3i underBlock = occupiedBlock.sub_(getBestCardinal(fallBlock.getUpVector()));
		List<Vec3i> underBlocks = getGoodCardinals(fallBlock.getUpVector());
		
		boolean notSupported = false;
		for (Vec3i v3 : underBlocks)
		{
			Vec3i inWorld = occupiedBlock.sub_(v3); 
			if (context.getBlock(inWorld).getId()=="Test:Air") {
				notSupported=true;
				break;
			}
		}

		 //LogManager.getLogger().info("InChunk
		 //"+String.valueOf(chunkCoords.x)+" "+String.valueOf(chunkCoords.y)+"
		 //"+String.valueOf(chunkCoords.z)+" "+String.valueOf(inChunkCoords.x)+"
		 ////"+String.valueOf(inChunkCoords.y)+"
		 //"+String.valueOf(inChunkCoords.z));
		 /*LogManager.getLogger().info("FallingBlock is at {},{},{}",
		 String.valueOf(occupiedBlock.x),
		 String.valueOf(occupiedBlock.y),
		 String.valueOf(occupiedBlock.z));*/
		 //LogManager.getLogger().info("Block is of type " +
		 //context.getWorldData().getChunk(chunkCoords).getBlock(inChunkCoords).getId());

		if (context.getBlock(underBlock) != null
		//		&& context.getBlock(underBlock).getId() != "Test:Air") {
			&& !notSupported) {
			LogManager.getLogger().info("Deleting FallingBlock at " + String.valueOf(occupiedBlock.x) + " " + String.valueOf(occupiedBlock.y) + " " + String.valueOf(occupiedBlock.z));
			context.setBlock(occupiedBlock, fallBlock2.getBlock());
			fallBlock.setInvisible();
			//server.invokeLater(() -> server.getWorld().getData().removeEntity(entity.getEntityId()));
			context.removeEntity(fallBlock);
		}
	}
}
