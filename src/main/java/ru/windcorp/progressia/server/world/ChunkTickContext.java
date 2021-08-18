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

package ru.windcorp.progressia.server.world;

import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.server.world.block.BlockTickContext;

public interface ChunkTickContext extends TickContext {

	Vec3i getChunk();

	default ChunkLogic getChunkLogic() {
		return getWorld().getChunk(getChunk());
	}

	default ChunkData getChunkData() {
		ChunkLogic chunkLogic = getChunkLogic();
		return chunkLogic == null ? null : chunkLogic.getData();
	}

	default void forEachBlock(Consumer<BlockTickContext> action) {
		TickContextMutable context = TickContextMutable.uninitialized();

		getChunkData().forEachBlock(blockInChunk -> {
			context.rebuild().withServer(getServer()).withChunk(getChunk()).withBlockInChunk(blockInChunk).build();
			action.accept(context);
		});
	}

}
