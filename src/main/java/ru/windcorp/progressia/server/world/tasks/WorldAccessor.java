package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.MultiLOC;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.ticking.TickerTask;

public class WorldAccessor {
	
	private final MultiLOC cache;
	{
		MultiLOC mloc = new MultiLOC();
		Consumer<TickerTask> disposer = mloc::release;
		
		cache = mloc
				.addClass(SetBlock.class, () -> new SetBlock(disposer))
				.addClass(AddOrRemoveTile.class, () -> new AddOrRemoveTile(disposer))
				.addClass(ChangeEntity.class, () -> new ChangeEntity(disposer))
				
				.addClass(BlockTriggeredUpdate.class, () -> new BlockTriggeredUpdate(disposer))
				.addClass(TileTriggeredUpdate.class, () -> new TileTriggeredUpdate(disposer));
	}
	
	private final Server server;
	
	public WorldAccessor(Server server) {
		this.server = server;
	}

	public void setBlock(Vec3i blockInWorld, BlockData block) {
		SetBlock change = cache.grab(SetBlock.class);
		change.initialize(blockInWorld, block);
		server.requestChange(change);
	}

	public void addTile(Vec3i blockInWorld, BlockFace face, TileData tile) {
		AddOrRemoveTile change = cache.grab(AddOrRemoveTile.class);
		change.initialize(blockInWorld, face, tile, true);
		server.requestChange(change);
	}

	public void removeTile(Vec3i blockInWorld, BlockFace face, TileData tile) {
		AddOrRemoveTile change = cache.grab(AddOrRemoveTile.class);
		change.initialize(blockInWorld, face, tile, false);
		server.requestChange(change);
	}

	public <T extends EntityData> void changeEntity(
			T entity, StateChange<T> stateChange
	) {
		ChangeEntity change = cache.grab(ChangeEntity.class);
		change.set(entity, stateChange);
		server.requestChange(change);
	}
	
	public void tickBlock(Vec3i blockInWorld) {
		// TODO
	}
	
	/**
	 * When a block is the trigger
	 * @param blockInWorld
	 */
	// TODO rename to something meaningful
	public void triggerUpdates(Vec3i blockInWorld) {
		BlockTriggeredUpdate evaluation = cache.grab(BlockTriggeredUpdate.class);
		evaluation.init(blockInWorld);
		server.requestEvaluation(evaluation);
	}
	
	/**
	 * When a tile is the trigger
	 * @param blockInWorld
	 * @param face
	 */
	// TODO rename to something meaningful
	public void triggerUpdates(Vec3i blockInWorld, BlockFace face) {
		TileTriggeredUpdate evaluation = cache.grab(TileTriggeredUpdate.class);
		evaluation.init(blockInWorld, face);
		server.requestEvaluation(evaluation);
	}

}
