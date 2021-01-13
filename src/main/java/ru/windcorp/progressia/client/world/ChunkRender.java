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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.graphics.model.Model;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.StaticModel;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.StaticModel.Builder;
import ru.windcorp.progressia.client.world.block.BlockRender;
import ru.windcorp.progressia.client.world.block.BlockRenderNone;
import ru.windcorp.progressia.client.world.block.BlockRenderRegistry;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizer;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerSupplier;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizers;
import ru.windcorp.progressia.client.world.tile.TileRender;
import ru.windcorp.progressia.client.world.tile.TileRenderRegistry;
import ru.windcorp.progressia.client.world.tile.TileRenderStack;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.generic.GenericChunk;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataStack;

public class ChunkRender
	implements GenericChunk<ChunkRender, BlockRender, TileRender, TileRenderStack> {

	private final WorldRender world;
	private final ChunkData data;

	private Model model = null;

	private final Map<TileDataStack, TileRenderStackImpl> tileRenderLists = Collections
		.synchronizedMap(new WeakHashMap<>());

	public ChunkRender(WorldRender world, ChunkData data) {
		this.world = world;
		this.data = data;
	}

	@Override
	public Vec3i getPosition() {
		return getData().getPosition();
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

	public ChunkData getData() {
		return data;
	}

	public synchronized void markForUpdate() {
		getWorld().markChunkForUpdate(getPosition());
	}

	public synchronized void render(ShapeRenderHelper renderer) {
		if (model == null) {
			return;
		}

		renderer.pushTransform().translate(
			data.getX() * ChunkData.BLOCKS_PER_CHUNK,
			data.getY() * ChunkData.BLOCKS_PER_CHUNK,
			data.getZ() * ChunkData.BLOCKS_PER_CHUNK
		);

		model.render(renderer);

		renderer.popTransform();
	}

	public synchronized void update() {
		Collection<ChunkRenderOptimizer> optimizers = ChunkRenderOptimizers.getAllSuppliers().stream()
			.map(ChunkRenderOptimizerSupplier::createOptimizer)
			.collect(Collectors.toList());

		optimizers.forEach(bro -> bro.startRender(this));

		StaticModel.Builder builder = StaticModel.builder();

		Vec3i cursor = new Vec3i();
		for (int x = 0; x < ChunkData.BLOCKS_PER_CHUNK; ++x) {
			for (int y = 0; y < ChunkData.BLOCKS_PER_CHUNK; ++y) {
				for (int z = 0; z < ChunkData.BLOCKS_PER_CHUNK; ++z) {
					cursor.set(x, y, z);

					buildBlock(cursor, optimizers, builder);
					buildBlockTiles(cursor, optimizers, builder);
				}
			}
		}

		optimizers.stream()
			.map(ChunkRenderOptimizer::endRender)
			.filter(Objects::nonNull)
			.forEach(builder::addPart);

		model = new StaticModel(builder);
	}

	private void buildBlock(
		Vec3i cursor,
		Collection<ChunkRenderOptimizer> optimizers,
		Builder builder
	) {
		BlockRender block = getBlock(cursor);

		if (block instanceof BlockRenderNone) {
			return;
		}

		forwardBlockToOptimizers(block, cursor, optimizers);

		if (!block.needsOwnRenderable()) {
			return;
		}

		addBlockRenderable(block, cursor, builder);
	}

	private void forwardBlockToOptimizers(
		BlockRender block,
		Vec3i cursor,
		Collection<ChunkRenderOptimizer> optimizers
	) {
		optimizers.forEach(cro -> cro.processBlock(block, cursor));
	}

	private void addBlockRenderable(
		BlockRender block,
		Vec3i cursor,
		Builder builder
	) {
		Renderable renderable = block.createRenderable();

		if (renderable == null) {
			renderable = block::render;
		}

		builder.addPart(
			renderable,
			new Mat4().identity().translate(cursor.x, cursor.y, cursor.z)
		);
	}

	private void buildBlockTiles(
		Vec3i cursor,
		Collection<ChunkRenderOptimizer> optimizers,
		Builder builder
	) {
		for (BlockFace face : BlockFace.getFaces()) {
			buildFaceTiles(cursor, face, optimizers, builder);
		}
	}

	private void buildFaceTiles(
		Vec3i cursor,
		BlockFace face,
		Collection<ChunkRenderOptimizer> optimizers,
		Builder builder
	) {
		List<TileData> tiles = getData().getTilesOrNull(cursor, face);

		if (tiles == null) {
			return;
		}

		for (int layer = 0; layer < tiles.size(); ++layer) {

			if (tiles.get(layer) == null) {
				System.out.println(tiles.get(layer).getId());
			}

			buildTile(
				cursor,
				face,
				TileRenderRegistry.getInstance().get(
					tiles.get(layer).getId()
				),
				layer,
				optimizers,
				builder
			);
		}
	}

	private void buildTile(
		Vec3i cursor,
		BlockFace face,
		TileRender tile,
		int layer,
		Collection<ChunkRenderOptimizer> optimizers,
		Builder builder
	) {
		// TODO implement

		Vec3 pos = new Vec3(cursor.x, cursor.y, cursor.z);

		optimizers.forEach(cro -> cro.processTile(tile, cursor, face));

		if (!tile.needsOwnRenderable())
			return;

		Vec3 offset = new Vec3(
			face.getVector().x,
			face.getVector().y,
			face.getVector().z
		);

		pos.add(offset.mul(1f / 64));

		builder.addPart(
			tile.createRenderable(face),
			new Mat4().identity().translate(pos)
		);
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
