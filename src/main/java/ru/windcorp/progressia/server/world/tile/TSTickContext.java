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

package ru.windcorp.progressia.server.world.tile;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.tile.TileDataStack;
import ru.windcorp.progressia.server.world.ChunkLogic;
import ru.windcorp.progressia.server.world.TickContextMutable;
import ru.windcorp.progressia.server.world.block.BlockTickContext;

public interface TSTickContext extends BlockTickContext {

	/*
	 * Specifications
	 */

	BlockFace getFace();

	/*
	 * Getters
	 */

	default TileLogicStack getTLSOrNull() {
		ChunkLogic chunkLogic = getChunkLogic();
		if (chunkLogic == null)
			return null;

		return chunkLogic.getTilesOrNull(getBlockInChunk(), getFace());
	}

	default TileLogicStack getTLS() {
		return getChunkLogic().getTiles(getBlockInChunk(), getFace());
	}

	default TileDataStack getTDSOrNull() {
		ChunkData chunkData = getChunkData();
		if (chunkData == null)
			return null;

		return chunkData.getTilesOrNull(getBlockInChunk(), getFace());
	}

	default TileDataStack getTDS() {
		return getChunkData().getTiles(getBlockInChunk(), getFace());
	}

	/*
	 * Contexts
	 */

	default TileTickContext forLayer(int layer) {
		return TickContextMutable.start().withServer(getServer()).withBlock(getBlockInWorld()).withFace(getFace())
				.withLayer(layer);
	}

	default boolean forEachTile(Consumer<TileTickContext> action) {
		TickContextMutable context = TickContextMutable.uninitialized();

		TileDataStack stack = getTDSOrNull();
		if (stack == null || stack.isEmpty())
			return false;

		for (int layer = 0; layer < stack.size(); ++layer) {
			context.rebuild().withServer(getServer()).withBlock(getBlockInWorld()).withFace(getFace()).withLayer(layer);
			action.accept(context);
		}

		return true;
	}

	default TSTickContext getComplementary() {
		return TickContextMutable.copyWorld(this).withBlock(getBlockInWorld().add_(getFace().getVector()))
				.withFace(getFace().getCounter()).build();
	}

	default <R> R evalComplementary(Function<TSTickContext, R> action) {
		Objects.requireNonNull(action, "action");
		return action.apply(getComplementary());
	}

	default void forComplementary(Consumer<TSTickContext> action) {
		Objects.requireNonNull(action, "action");
		evalComplementary((Function<TSTickContext, Void>) ctxt -> {
			action.accept(ctxt);
			return null;
		});
	}

}
