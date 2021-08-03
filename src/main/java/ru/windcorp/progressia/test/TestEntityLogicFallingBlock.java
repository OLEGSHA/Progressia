package ru.windcorp.progressia.test;

import java.util.HashSet;
import java.util.Set;

import org.apache.logging.log4j.LogManager;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.ClientState;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.TickContext;
import ru.windcorp.progressia.server.world.entity.EntityLogic;

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
	}

	public TestEntityLogicFallingBlock(String id) {
		super(id);
		addFallables();
	}

	private Vec3i trueMod(Vec3i input, Vec3i modulus) // Move this to a class in
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
	}

	@Override
	public void tick(EntityData entity, TickContext context) { // context.getWorldData()
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

		TestEntityDataFallingBlock fallBlock = (TestEntityDataFallingBlock) ClientState.getInstance().getWorld()
				.getData().getEntity(entity.getEntityId()); // ClientState.getInstance().getWorld().getData().getEntity(entity.getEntityId());
		// fallBlock = (TestEntityDataFallingBlock) entity;

		// LogManager.getLogger().info("NotNull FB
		// "+String.valueOf(fallBlock!=null));
		if (fallBlock == null) {
			return;
		}

		if (fallBlock.isDone() || !context.getWorld().isBlockLoaded(fallBlock.getBlockInWorld(null))) {
			return;
		}

		if (!fallBlock.hasDestroyed()) {
			// LogManager.getLogger().info(fallBlock.getStartPos());
			context.getAccessor().setBlock(fallBlock.getBlockInWorld(null),
					BlockDataRegistry.getInstance().get("Test:Air"));
			fallBlock.setDestroyed();
		}

		Vec3i occupiedBlock = fallBlock.getBlockInWorld(null);
		Vec3i underBlock = occupiedBlock.sub_(0, 0, 1);

		Vec3i chunkCoords = trueDiv(underBlock, new Vec3i(16));
		Vec3i inChunkCoords = trueMod(underBlock, new Vec3i(16));

		// LogManager.getLogger().info("InChunk
		// "+String.valueOf(chunkCoords.x)+" "+String.valueOf(chunkCoords.y)+"
		// "+String.valueOf(chunkCoords.z)+" "+String.valueOf(inChunkCoords.x)+"
		// "+String.valueOf(inChunkCoords.y)+"
		// "+String.valueOf(inChunkCoords.z));
		// LogManager.getLogger().info("FallingBlock is at
		// "+String.valueOf(occupiedBlock.x)+"
		// "+String.valueOf(occupiedBlock.y)+"
		// "+String.valueOf(occupiedBlock.z));
		// LogManager.getLogger().info("Block is of type " +
		// context.getWorldData().getChunk(chunkCoords).getBlock(inChunkCoords).getId());

		if (context.getWorldData().isBlockLoaded(occupiedBlock)
				&& context.getWorldData().getChunk(chunkCoords).getBlock(inChunkCoords).getId() != "Test:Air") {
			LogManager.getLogger().info("Deleting FallingBlock at " + String.valueOf(occupiedBlock.x) + " " + String.valueOf(occupiedBlock.y) + " " + String.valueOf(occupiedBlock.z));
			// ClientState.getInstance().getWorld().getData().setBlock(occupiedBlock,
			// fallBlock.getBlock(),true);
			context.getAccessor().setBlock(occupiedBlock, fallBlock.getBlock());
			fallBlock.setInvisible(); // Until I know how to properly delete it.
			//ClientState.getInstance().getWorld().getData().removeEntity(entity.getEntityId());// context.getWorldData().removeEntity(entity.getEntityId());
			Server server = context.getServer();
			server.invokeLater(() -> server.getWorld().getData().removeEntity(entity.getEntityId()));
		}
	}
}
