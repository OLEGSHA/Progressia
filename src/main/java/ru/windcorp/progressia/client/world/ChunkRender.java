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

package ru.windcorp.progressia.client.world;

import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.world.block.BlockRender;
import ru.windcorp.progressia.client.world.block.BlockRenderRegistry;
import ru.windcorp.progressia.client.world.tile.TileRender;
import ru.windcorp.progressia.client.world.tile.TileRenderRegistry;
import ru.windcorp.progressia.client.world.tile.TileRenderStack;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.generic.GenericChunk;
import ru.windcorp.progressia.common.world.tile.TileDataStack;

public class ChunkRender implements GenericChunk<ChunkRender, BlockRender, TileRender, TileRenderStack> {

	private final WorldRender world;
	private final ChunkData data;

	private final ChunkRenderModel model;

	private final Map<TileDataStack, TileRenderStackImpl> tileRenderLists = Collections
			.synchronizedMap(new WeakHashMap<>());

	public ChunkRender(WorldRender world, ChunkData data) {
		this.world = world;
		this.data = data;
		this.model = new ChunkRenderModel(this);
	}

	@Override
	public Vec3i getPosition() {
		return getData().getPosition();
	}

	@Override
	public BlockRender getBlock(Vec3i posInChunk) {
		return BlockRenderRegistry.getInstance().get(getData().getBlock(posInChunk).getId());
	}

	@Override
	public TileRenderStack getTiles(Vec3i blockInChunk, BlockFace face) {
		return getTileStackWrapper(getData().getTiles(blockInChunk, face));
	}

	@Override
	public boolean hasTiles(Vec3i blockInChunk, BlockFace face) {
		return getData().hasTiles(blockInChunk, face);
	}

	private TileRenderStack getTileStackWrapper(TileDataStack tileDataList) {
		return tileRenderLists.computeIfAbsent(tileDataList, TileRenderStackImpl::new);
	}

	public WorldRender getWorld() {
		return world;
	}

	public ChunkData getData() {
		return data;
	}

	public synchronized void markForUpdate() {
		getWorld().markChunkForUpdate(getPosition());
	}

	public synchronized void render(ShapeRenderHelper renderer) {
		model.render(renderer);
	}

	public synchronized void update() {
		model.update();
	}

	private class TileRenderStackImpl extends TileRenderStack {

		private final TileDataStack parent;

		public TileRenderStackImpl(TileDataStack parent) {
			this.parent = parent;
		}

		@Override
		public Vec3i getBlockInChunk(Vec3i output) {
			return parent.getBlockInChunk(output);
		}

		@Override
		public ChunkRender getChunk() {
			return ChunkRender.this;
		}

		@Override
		public BlockFace getFace() {
			return parent.getFace();
		}

		@Override
		public TileRender get(int index) {
			return TileRenderRegistry.getInstance().get(parent.get(index).getId());
		}

		@Override
		public int size() {
			return parent.size();
		}

		@Override
		public TileDataStack getData() {
			return parent;
		}

	}

}
