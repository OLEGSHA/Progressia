/*******************************************************************************
 * Optica
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
package ru.windcorp.optica.client.world;

import java.util.HashMap;
import java.util.Map;

import glm.mat._4.Mat4;
import ru.windcorp.optica.client.graphics.model.Model;
import ru.windcorp.optica.client.graphics.model.Shape;
import ru.windcorp.optica.client.graphics.model.StaticModel;
import ru.windcorp.optica.client.graphics.model.StaticModel.Builder;
import ru.windcorp.optica.client.graphics.model.WorldRenderable;
import ru.windcorp.optica.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.optica.client.world.renders.BlockRender;
import ru.windcorp.optica.client.world.renders.BlockRenderNone;
import ru.windcorp.optica.client.world.renders.BlockRenders;
import ru.windcorp.optica.client.world.renders.bro.BlockRenderOptimizer;
import ru.windcorp.optica.client.world.renders.bro.BlockRenderOptimizerGenerator;
import ru.windcorp.optica.client.world.renders.bro.BlockRenderOptimizerGenerators;
import ru.windcorp.optica.common.world.ChunkData;

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
	
	public BlockRender getBlock(int xInChunk, int yInChunk, int zInChunk) {
		return BlockRenders.get(
				getData().getBlock(xInChunk, yInChunk, zInChunk).getId()
		);
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
		Map<String, BlockRenderOptimizer> optimizers = new HashMap<>();
		
		for (
				BlockRenderOptimizerGenerator generator :
				BlockRenderOptimizerGenerators.getAll()
		) {
			BlockRenderOptimizer optimizer = generator.createOptimizer();
			optimizers.put(generator.getId(), optimizer);
			optimizer.startRender(this);
		}
		
		StaticModel.Builder builder = StaticModel.builder();
		
		for (int x = 0; x < ChunkData.BLOCKS_PER_CHUNK; ++x) {
			for (int y = 0; y < ChunkData.BLOCKS_PER_CHUNK; ++y) {
				for (int z = 0; z < ChunkData.BLOCKS_PER_CHUNK; ++z) {
					
					BlockRender block = getBlock(x, y, z);
					
					if (block instanceof BlockRenderNone) {
						continue;
					}
					
					if (tryToForwardToOptimizers(block, x, y, z, optimizers)) {
						continue;
					}
					
					if (tryToCreateRenderable(block, x, y, z, builder)) {
						continue;
					}
					
					addRenderAsRenderable(block, x, y, z, builder);
				}
			}
		}
		
		for (BlockRenderOptimizer optimizer : optimizers.values()) {
			Shape result = optimizer.endRender();
			if (result != null) {
				builder.addPart(result);
			}
		}
		
		model = new StaticModel(builder);
		needsUpdate = false;
	}

	private boolean tryToForwardToOptimizers(
			BlockRender block, int x, int y, int z,
			Map<String, BlockRenderOptimizer> optimizers
	) {
		if (!block.isOptimized()) {
			return false;
		}
		BlockRenderOptimizer optimizer = optimizers.get(block.getOptimizer());
		
		if (optimizer == null) {
			return false;
		}
		
		optimizer.processBlock(block, x, y, z);
		
		return true;
	}

	private boolean tryToCreateRenderable(
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

	private void addRenderAsRenderable(
			BlockRender block, int x, int y, int z,
			Builder builder
	) {
		builder.addPart(
				block::render,
				new Mat4().identity().translate(x, y, z)
		);
	}

}
