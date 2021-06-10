/*
 * Progressia
 * Copyright (C)  2020-2021  Wind Corporation and contributors
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package ru.windcorp.progressia.server.world.tasks;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.MultiLOC;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataRegistry;
import ru.windcorp.progressia.server.Server;
import ru.windcorp.progressia.server.world.ticking.TickerTask;

public class WorldAccessor {

	private final MultiLOC cache;
	{
		MultiLOC mloc = new MultiLOC();
		Consumer<TickerTask> disposer = mloc::release;

		cache = mloc.addClass(SetBlock.class, () -> new SetBlock(disposer))
				.addClass(AddTile.class, () -> new AddTile(disposer))
				.addClass(RemoveTile.class, () -> new RemoveTile(disposer))
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
		change.getPacket().set(block, blockInWorld);
		server.requestChange(change);
	}

	public void setBlock(Vec3i blockInWorld, String id) {
		setBlock(blockInWorld, BlockDataRegistry.getInstance().get(id));
	}

	public void addTile(Vec3i blockInWorld, BlockFace face, TileData tile) {
		AddTile change = cache.grab(AddTile.class);
		change.getPacket().set(tile, blockInWorld, face);
		server.requestChange(change);
	}

	public void addTile(Vec3i blockInWorld, BlockFace face, String id) {
		addTile(blockInWorld, face, TileDataRegistry.getInstance().get(id));
	}

	public void removeTile(Vec3i blockInWorld, BlockFace face, int tag) {
		RemoveTile change = cache.grab(RemoveTile.class);
		change.getPacket().set(blockInWorld, face, tag);
		server.requestChange(change);
	}

	public <T extends EntityData> void changeEntity(T entity, StateChange<T> stateChange) {
		ChangeEntity change = cache.grab(ChangeEntity.class);
		change.set(entity, stateChange);
		server.requestChange(change);
	}

	public void tickBlock(Vec3i blockInWorld) {
		// TODO
	}

	/**
	 * When a block is the trigger
	 * 
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
	 * 
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
