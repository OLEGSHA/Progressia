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
import ru.windcorp.progressia.client.world.tile.TileRenderReference;
import ru.windcorp.progressia.client.world.tile.TileRenderRegistry;
import ru.windcorp.progressia.client.world.tile.TileRenderStack;
import ru.windcorp.progressia.common.world.DefaultChunkData;
import ru.windcorp.progressia.common.world.TileDataReference;
import ru.windcorp.progressia.common.world.TileDataStack;
import ru.windcorp.progressia.common.world.generic.ChunkGenericRO;
import ru.windcorp.progressia.common.world.rels.AbsFace;
import ru.windcorp.progressia.common.world.rels.BlockFace;
import ru.windcorp.progressia.common.world.rels.RelFace;

public class ChunkRender
	implements ChunkGenericRO<BlockRender, TileRender, TileRenderStack, TileRenderReference, ChunkRender> {

	private final WorldRender world;
	private final DefaultChunkData data;

	private final ChunkRenderModel model;

	private final Map<TileDataStack, TileRenderStackImpl> tileRenderLists = Collections
		.synchronizedMap(new WeakHashMap<>());

	public ChunkRender(WorldRender world, DefaultChunkData data) {
		this.world = world;
		this.data = data;
		this.model = new ChunkRenderModel(this);
	}

	@Override
	public Vec3i getPosition() {
		return getData().getPosition();
	}
	
	@Override
	public AbsFace getUp() {
		return getData().getUp();
	}

	@Override
	public BlockRender getBlock(Vec3i posInChunk) {
		return BlockRenderRegistry.getInstance().get(
			getData().getBlock(posInChunk).getId()
		);
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
		return tileRenderLists.computeIfAbsent(
			tileDataList,
			TileRenderStackImpl::new
		);
	}

	public WorldRender getWorld() {
		return world;
	}

	public DefaultChunkData getData() {
		return data;
	}

	public void markForUpdate() {
		getWorld().markChunkForUpdate(getPosition());
	}

	public synchronized void render(ShapeRenderHelper renderer) {
		if (!data.isEmpty) {
			model.render(renderer);
		}
	}

	public synchronized void update() {
		model.update();
	}

	private class TileRenderStackImpl extends TileRenderStack {
		private class TileRenderReferenceImpl implements TileRenderReference {
			private final TileDataReference parent;

			public TileRenderReferenceImpl(TileDataReference parent) {
				this.parent = parent;
			}

			@Override
			public TileRender get() {
				return TileRenderRegistry.getInstance().get(parent.get().getId());
			}

			@Override
			public int getIndex() {
				return parent.getIndex();
			}

			@Override
			public TileRenderStack getStack() {
				return TileRenderStackImpl.this;
			}
		}

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
		public RelFace getFace() {
			return parent.getFace();
		}
		
		@Override
		public TileRenderReference getReference(int index) {
			return new TileRenderReferenceImpl(parent.getReference(index));
		}

		@Override
		public int getIndexByTag(int tag) {
			return parent.getIndexByTag(tag);
		}

		@Override
		public int getTagByIndex(int index) {
			return parent.getTagByIndex(index);
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
