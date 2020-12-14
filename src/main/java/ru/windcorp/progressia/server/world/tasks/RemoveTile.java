package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileDataStack;

class RemoveTile extends CachedWorldChange {
	
	private final Vec3i blockInWorld = new Vec3i();
	private BlockFace face;
	private int tag;

	public RemoveTile(Consumer<? super CachedChange> disposer) {
		super(disposer, "Core:RemoveTile");
	}

	public void initialize(
			Vec3i position, BlockFace face,
			int tag
	) {
		this.blockInWorld.set(position.x, position.y, position.z);
		this.face = face;
		this.tag = tag;
	}
	
	@Override
	protected void affectCommon(WorldData world) {
		TileDataStack tiles = world
				.getChunkByBlock(blockInWorld)
				.getTiles(Coordinates.convertInWorldToInChunk(blockInWorld, null), face);

		tiles.remove(tiles.getIndexByTag(tag));
	}
	
	@Override
	public void getRelevantChunk(Vec3i output) {
		Coordinates.convertInWorldToChunk(blockInWorld, output);
	}

}