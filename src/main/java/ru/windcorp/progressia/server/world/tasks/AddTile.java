package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.WorldData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataStack;

class AddTile extends CachedWorldChange {
	
	private final Vec3i blockInWorld = new Vec3i();
	private BlockFace face;
	private TileData tile;

	public AddTile(Consumer<? super CachedChange> disposer) {
		super(disposer, "Core:AddTile");
	}

	public void initialize(
			Vec3i position, BlockFace face,
			TileData tile
	) {
		if (this.tile != null)
			throw new IllegalStateException("Payload is not null. Current: " + this.tile + "; requested: " + tile);
		
		this.blockInWorld.set(position.x, position.y, position.z);
		this.face = face;
		this.tile = tile;
	}
	
	@Override
	protected void affectCommon(WorldData world) {
		TileDataStack tiles = world
				.getChunkByBlock(blockInWorld)
				.getTiles(Coordinates.convertInWorldToInChunk(blockInWorld, null), face);

		tiles.add(tile);
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
		this.tile = null;
	}

}