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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

import glm.mat._4.Mat4;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.graphics.model.Model;
import ru.windcorp.progressia.client.graphics.model.Renderable;
import ru.windcorp.progressia.client.graphics.model.ShapeRenderHelper;
import ru.windcorp.progressia.client.graphics.model.StaticModel;
import ru.windcorp.progressia.client.graphics.model.StaticModel.Builder;
import ru.windcorp.progressia.client.world.block.BlockRender;
import ru.windcorp.progressia.client.world.block.BlockRenderNone;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizer;
import ru.windcorp.progressia.client.world.cro.ChunkRenderOptimizerRegistry;
import ru.windcorp.progressia.client.world.tile.TileRender;
import ru.windcorp.progressia.client.world.tile.TileRenderNone;
import ru.windcorp.progressia.client.world.tile.TileRenderStack;
import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.rels.AxisRotations;
import ru.windcorp.progressia.common.world.rels.RelFace;

public class ChunkRenderModel implements Renderable {
	
	private final ChunkRender chunk;
	
	private final Collection<ChunkRenderOptimizer> optimizers = new ArrayList<>();
	private Model model = null;

	public ChunkRenderModel(ChunkRender chunk) {
		this.chunk = chunk;
	}

	@Override
	public void render(ShapeRenderHelper renderer) {
		if (model == null) return;
		
		float offset = ChunkData.BLOCKS_PER_CHUNK / 2 - 0.5f;
		
		renderer.pushTransform().translate(
			chunk.getX() * ChunkData.BLOCKS_PER_CHUNK,
			chunk.getY() * ChunkData.BLOCKS_PER_CHUNK,
			chunk.getZ() * ChunkData.BLOCKS_PER_CHUNK
		).translate(offset, offset, offset)
		.mul(AxisRotations.getResolutionMatrix4(chunk.getUp()))
		.translate(-offset, -offset, -offset);

		model.render(renderer);

		renderer.popTransform();
	}
	
	public void update() {
		setupCROs();
		
		StaticModel.Builder sink = StaticModel.builder();
		
		optimizers.forEach(ChunkRenderOptimizer::startRender);
		
		chunk.forEachBiC(relBlockInChunk -> {
			processBlockAndTiles(relBlockInChunk, sink);
		});
		
		for (ChunkRenderOptimizer optimizer : optimizers) {
			Renderable renderable = optimizer.endRender();
			if (renderable != null) {
				sink.addPart(renderable);
			}
		}
		
		this.model = sink.build();
		this.optimizers.clear();
	}

	private void setupCROs() {
		Set<String> ids = ChunkRenderOptimizerRegistry.getInstance().keySet();
		
		for (String id : ids) {
			ChunkRenderOptimizer optimizer = ChunkRenderOptimizerRegistry.getInstance().create(id);
			optimizer.setup(chunk);
			this.optimizers.add(optimizer);
		}
	}

	private void processBlockAndTiles(Vec3i relBlockInChunk, Builder sink) {
		processBlock(relBlockInChunk, sink);
		
		for (RelFace face : RelFace.getFaces()) {
			processTileStack(relBlockInChunk, face, sink);
		}
	}

	private void processBlock(Vec3i relBlockInChunk, Builder sink) {
		BlockRender block = chunk.getBlockRel(relBlockInChunk);
		
		if (block instanceof BlockRenderNone) {
			return;
		}
		
		if (block.needsOwnRenderable()) {
			sink.addPart(
				block.createRenderable(chunk.getData(), relBlockInChunk), 
				new Mat4().identity().translate(relBlockInChunk.x, relBlockInChunk.y, relBlockInChunk.z)
			);
		}
		
		processBlockWithCROs(block, relBlockInChunk);
	}

	private void processBlockWithCROs(BlockRender block, Vec3i relBlockInChunk) {
		for (ChunkRenderOptimizer optimizer : optimizers) {
			optimizer.addBlock(block, relBlockInChunk);
		}
	}

	private void processTileStack(Vec3i relBlockInChunk, RelFace face, Builder sink) {
		TileRenderStack trs = chunk.getTilesOrNullRel(relBlockInChunk, face);
		
		if (trs == null || trs.isEmpty()) {
			return;
		}
		
		trs.forEach(tile -> processTile(tile, relBlockInChunk, face, sink));
	}

	private void processTile(TileRender tile, Vec3i relBlockInChunk, RelFace face, Builder sink) {	
		if (tile instanceof TileRenderNone) {
			return;
		}
		
		if (tile.needsOwnRenderable()) {
			sink.addPart(
				tile.createRenderable(chunk.getData(), relBlockInChunk, face), 
				new Mat4().identity().translate(relBlockInChunk.x, relBlockInChunk.y, relBlockInChunk.z)
			);
		}
		
		processTileWithCROs(tile, relBlockInChunk, face);
	}

	private void processTileWithCROs(TileRender tile, Vec3i relBlockInChunk, RelFace face) {
		for (ChunkRenderOptimizer optimizer : optimizers) {
			optimizer.addTile(tile, relBlockInChunk, face);
		}
	}

}
