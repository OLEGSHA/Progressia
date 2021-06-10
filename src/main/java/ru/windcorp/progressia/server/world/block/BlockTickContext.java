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

package ru.windcorp.progressia.server.world.block;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.block.BlockRelation;
import ru.windcorp.progressia.server.world.ChunkTickContext;
import ru.windcorp.progressia.server.world.TickContextMutable;
import ru.windcorp.progressia.server.world.tile.TSTickContext;

public interface BlockTickContext extends ChunkTickContext {

	/**
	 * Returns the current world coordinates.
	 * 
	 * @return the world coordinates of the block being ticked
	 */
	Vec3i getBlockInWorld();

	default Vec3i getBlockInChunk() {
		return Coordinates.convertInWorldToInChunk(getBlockInWorld(), null);
	}

	default BlockLogic getBlock() {
		return getWorld().getBlock(getBlockInWorld());
	}

	default BlockData getBlockData() {
		return getWorldData().getBlock(getBlockInWorld());
	}

	default void forEachFace(Consumer<TSTickContext> action) {
		Objects.requireNonNull(action, "action");
		TickContextMutable context = TickContextMutable.uninitialized();

		for (BlockFace face : BlockFace.getFaces()) {
			context.rebuild().withServer(getServer()).withBlock(getBlockInWorld()).withFace(face).build();
			action.accept(context);
		}
	}

	default BlockTickContext getNeighbor(Vec3i direction) {
		Objects.requireNonNull(direction, "direction");
		return TickContextMutable.copyWorld(this).withBlock(getBlockInWorld().add_(direction)).build();
	}

	default BlockTickContext getNeighbor(BlockRelation relation) {
		Objects.requireNonNull(relation, "relation");
		return getNeighbor(relation.getVector());
	}

	default <R> R evalNeighbor(Vec3i direction, Function<BlockTickContext, R> action) {
		Objects.requireNonNull(action, "action");
		Objects.requireNonNull(direction, "direction");
		return action.apply(getNeighbor(direction));
	}

	default <R> R evalNeighbor(BlockRelation relation, Function<BlockTickContext, R> action) {
		Objects.requireNonNull(action, "action");
		Objects.requireNonNull(relation, "relation");
		return evalNeighbor(relation.getVector(), action);
	}

	default void forNeighbor(Vec3i direction, Consumer<BlockTickContext> action) {
		Objects.requireNonNull(action, "action");
		Objects.requireNonNull(direction, "direction");
		evalNeighbor(direction, (Function<BlockTickContext, Void>) ctxt -> {
			action.accept(ctxt);
			return null;
		});
	}

	default void forNeighbor(BlockRelation relation, Consumer<BlockTickContext> action) {
		Objects.requireNonNull(action, "action");
		Objects.requireNonNull(relation, "relation");
		forNeighbor(relation.getVector(), action);
	}

	/*
	 * Convenience methods - changes
	 */

	default void setThisBlock(BlockData block) {
		getAccessor().setBlock(getBlockInWorld(), block);
	}

	default void setThisBlock(String id) {
		getAccessor().setBlock(getBlockInWorld(), id);
	}

}
