/*******************************************************************************
 * Progressia
 * Copyright (C) 2020  Wind Corporation
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
 *******************************************************************************/
package ru.windcorp.progressia.client.world;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import glm.mat._4.Mat4;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.graphics.model.Model;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.StaticModel;
import ru.windcorp.progressia.client.graphics.model.WorldRenderable;
import ru.windcorp.progressia.client.graphics.model.StaticModel.Builder;
import ru.windcorp.progressia.client.world.renders.BlockRender;
import ru.windcorp.progressia.client.world.renders.BlockRenderNone;
import ru.windcorp.progressia.client.world.renders.BlockRenders;
import ru.windcorp.progressia.client.world.renders.TileRender;
import ru.windcorp.progressia.client.world.renders.TileRenders;
import ru.windcorp.progressia.client.world.renders.cro.ChunkRenderOptimizer;
import ru.windcorp.progressia.client.world.renders.cro.ChunkRenderOptimizerSupplier;
import ru.windcorp.progressia.client.world.renders.cro.ChunkRenderOptimizers;
import ru.windcorp.progressia.common.block.BlockFace;
import ru.windcorp.progressia.common.block.TileData;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.ChunkData;

public class ChunkRender {

	private final WorldRender world;
	private final ChunkData data;

	private boolean needsUpdate;
	private Model model = null;
	
	public ChunkRender(WorldRender world, ChunkData data) {
		this.world = world;
		this.data = data;
	}
	
	public WorldRender getWorld() {
		return world;
	}

	public ChunkData getData() {
		return data;
	}
	
	public void markForUpdate() {
		this.needsUpdate = true;
	}
	
	public boolean needsUpdate() {
		return needsUpdate;
	}
	
	public BlockRender getBlock(Vec3i posInChunk) {
		return BlockRenders.get(getData().getBlock(posInChunk).getId());
	}
	
	public void render(ShapeRenderHelper renderer) {
		if (model == null || needsUpdate()) {
			buildModel();
		}
		
		renderer.pushTransform().translate(
				data.getX() * ChunkData.BLOCKS_PER_CHUNK,
				data.getY() * ChunkData.BLOCKS_PER_CHUNK,
				data.getZ() * ChunkData.BLOCKS_PER_CHUNK
		);
		
		model.render(renderer);
		
		renderer.popTransform();
	}

	private void buildModel() {
		Collection<ChunkRenderOptimizer> optimizers =
				ChunkRenderOptimizers.getAllSuppliers().stream()
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
		
		for (ChunkRenderOptimizer optimizer : optimizers) {
			Shape result = optimizer.endRender();
			if (result != null) {
				builder.addPart(result);
			}
		}
		
		model = new StaticModel(builder);
		needsUpdate = false;
	}

	private void buildBlock(
			Vec3i cursor,
			Collection<ChunkRenderOptimizer> optimizers,
			Builder builder
	) {
		BlockRender block = getBlock(cursor);
		int x = cursor.x;
		int y = cursor.y;
		int z = cursor.z;
		
		if (block instanceof BlockRenderNone) {
			return;
		}
		
		forwardBlockToOptimizers(block, x, y, z, optimizers);
		
		if (!block.needsOwnRenderable()) {
			return;
		}
		
		if (tryToCreateBlockRenderable(block, x, y, z, builder)) {
			return;
		}
		
		addBlockRenderAsRenderable(block, x, y, z, builder);
	}

	private void forwardBlockToOptimizers(
			BlockRender block, int x, int y, int z,
			Collection<ChunkRenderOptimizer> optimizers
	) {
		optimizers.forEach(bro -> bro.processBlock(block, x, y, z));
	}

	private boolean tryToCreateBlockRenderable(
			BlockRender block, int x, int y, int z,
			Builder builder
	) {
		WorldRenderable renderable = block.createRenderable();
		
		if (renderable == null) {
			return false;
		}
		
		builder.addPart(renderable, new Mat4().identity().translate(x, y, z));
		return true;
	}

	private void addBlockRenderAsRenderable(
			BlockRender block, int x, int y, int z,
			Builder builder
	) {
		builder.addPart(
				block::render,
				new Mat4().identity().translate(x, y, z)
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
			Vec3i cursor, BlockFace face,
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
					cursor, face,
					TileRenders.get(tiles.get(layer).getId()),
					layer,
					optimizers, builder
			);
		}
	}

	private void buildTile(
			Vec3i cursor, BlockFace face,
			TileRender tile,
			int layer,
			Collection<ChunkRenderOptimizer> optimizers,
			Builder builder
	) {
		// TODO implement
		
		Vec3 pos = Vectors.grab3().set(cursor.x, cursor.y, cursor.z);
		
		Vec3 offset = Vectors.grab3().set(
				face.getVector().x, face.getVector().y, face.getVector().z
		);
		
		pos.add(offset.mul(1f / 64));
		
		builder.addPart(
				tile.createRenderable(face),
				new Mat4().identity().translate(pos)
		);
		
		Vectors.release(pos);
		Vectors.release(offset);
	}

}
