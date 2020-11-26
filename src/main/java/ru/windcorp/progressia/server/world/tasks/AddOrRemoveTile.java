package ru.windcorp.progressia.server.world.tasks;

import java.util.List;
import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;

class AddOrRemoveTile extends CachedWorldChange {
	
	private final Vec3i blockInWorld = new Vec3i();
	private BlockFace face;
	private TileData tile;

	private boolean shouldAdd;

	public AddOrRemoveTile(Consumer<? super CachedChange> disposer) {
		super(disposer, "Core:AddOrRemoveTile");
	}

	public void initialize(
			Vec3i position, BlockFace face,
			TileData tile,
			boolean shouldAdd
	) {
		if (this.tile != null)
			throw new IllegalStateException("Payload is not null. Current: " + this.tile + "; requested: " + tile);
		
		this.blockInWorld.set(position.x, position.y, position.z);
		this.face = face;
		this.tile = tile;
		this.shouldAdd = shouldAdd;
	}
	
	@Override
	protected void affectCommon(WorldData world) {
		Vec3i blockInChunk = Vectors.grab3i();
		Coordinates.convertInWorldToInChunk(blockInWorld, blockInChunk);

		List<TileData> tiles = world.getChunkByBlock(blockInWorld).getTiles(blockInChunk, face);

		if (shouldAdd) {
			tiles.add(tile);
		} else {
			tiles.remove(tile);
		}

		Vectors.release(blockInChunk);
	}
	
	@Override
	public void getRelevantChunk(Vec3i output) {
		Coordinates.convertInWorldToChunk(blockInWorld, output);
	}
	
	@Override
	public void dispose() {
		super.dispose();
		this.tile = null;
	}

}