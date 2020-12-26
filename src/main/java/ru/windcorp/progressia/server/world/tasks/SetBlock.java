package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockData;

class SetBlock extends CachedWorldChange {

	private final Vec3i blockInWorld = new Vec3i();
	private BlockData block;

	public SetBlock(Consumer<? super CachedChange> disposer) {
		super(disposer, "Core:SetBlock");
	}

	public void initialize(Vec3i blockInWorld, BlockData block) {
		if (this.block != null)
			throw new IllegalStateException("Payload is not null. Current: " + this.block + "; requested: " + block);
		
		this.blockInWorld.set(blockInWorld.x, blockInWorld.y, blockInWorld.z);
		this.block = block;
	}
	
	@Override
	protected void affectCommon(WorldData world) {
		world
			.getChunkByBlock(blockInWorld)
			.setBlock(Coordinates.convertInWorldToInChunk(blockInWorld, null), block, true);
	}

	@Override
	public void getRelevantChunk(Vec3i output) {
		Coordinates.convertInWorldToChunk(blockInWorld, output);
	}
	
	@Override
	protected Vec3i getAffectedChunk(Vec3i output) {
		getRelevantChunk(output);
		return output;
	}
	
	@Override
	public void dispose() {
		super.dispose();
		this.block = null;
	}

}