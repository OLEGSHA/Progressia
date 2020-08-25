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
package ru.windcorp.progressia.client.world.renders.cro;

import static ru.windcorp.progressia.common.world.ChunkData.BLOCKS_PER_CHUNK;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.Face;
import ru.windcorp.progressia.client.graphics.model.Faces;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.ChunkRender;
import ru.windcorp.progressia.client.world.renders.BlockRender;
import ru.windcorp.progressia.common.block.BlockFace;

public class ChunkRenderOptimizerCube extends ChunkRenderOptimizer {
	
	public static interface OpaqueCube {
		public Texture getTexture(BlockFace face);
		public boolean isOpaque(BlockFace face);
		public boolean isBlockOpaque();
	}
	
	private static final Vec3 COLOR_MULTIPLIER = new Vec3(1, 1, 1);
	
	private final OpaqueCube[][][] data =
			new OpaqueCube[BLOCKS_PER_CHUNK]
			              [BLOCKS_PER_CHUNK]
			              [BLOCKS_PER_CHUNK];

	@Override
	public void startRender(ChunkRender chunk) {
		// Do nothing
	}

	@Override
	public void processBlock(BlockRender block, int x, int y, int z) {
		if (!(block instanceof OpaqueCube)) return;
		OpaqueCube opaqueCube = (OpaqueCube) block;
		addBlock(x, y, z, opaqueCube);
	}
	
	protected void addBlock(int x, int y, int z, OpaqueCube cube) {
		data[x][y][z] = cube;
	}
	
	protected OpaqueCube getBlock(Vec3i cursor) {
		return data[cursor.x][cursor.y][cursor.z];
	}

	@Override
	public Shape endRender() {
		
		Collection<Face> shapeFaces = new ArrayList<>(
				BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK * 3
		);
		
		Vec3i cursor = new Vec3i();
		
		for (cursor.x = 0; cursor.x < BLOCKS_PER_CHUNK; ++cursor.x) {
			for (cursor.y = 0; cursor.y < BLOCKS_PER_CHUNK; ++cursor.y) {
				for (cursor.z = 0; cursor.z < BLOCKS_PER_CHUNK; ++cursor.z) {
					OpaqueCube block = getBlock(cursor);
					
					if (block == null) continue;
					
					processInnerFaces(block, cursor, shapeFaces::add);
					processOuterFaces(block, cursor, shapeFaces::add);
				}
			}
		}
		
		return new Shape(
				Usage.STATIC,
				WorldRenderProgram.getDefault(),
				shapeFaces.toArray(new Face[shapeFaces.size()])
		);
	}

	private void processInnerFaces(
			OpaqueCube block,
			Vec3i cursor,
			Consumer<Face> output
	) {
		if (block.isBlockOpaque()) return;
		
		for (BlockFace face : BlockFace.getFaces()) {
			
			Texture texture = block.getTexture(face);
			if (texture == null) continue;
			
			output.accept(Faces.createBlockFace(
					WorldRenderProgram.getDefault(),
					texture,
					COLOR_MULTIPLIER,
					new Vec3(cursor.x, cursor.y, cursor.z),
					face,
					true
			));
			
		}
	}
	
	private void processOuterFaces(
			OpaqueCube block,
			Vec3i cursor,
			Consumer<Face> output
	) {
		for (BlockFace face : BlockFace.getFaces()) {
			
			Texture texture = block.getTexture(face);
			if (texture == null) continue;
			
			if (!shouldRenderFace(cursor, face)) continue;
			
			output.accept(Faces.createBlockFace(
					WorldRenderProgram.getDefault(),
					texture,
					COLOR_MULTIPLIER,
					new Vec3(cursor.x, cursor.y, cursor.z),
					face,
					false
			));
			
		}
	}
	
	private boolean shouldRenderFace(Vec3i cursor, BlockFace face) {
		cursor.add(face.getVector());
		try {
			
			// TODO handle neighboring chunks properly
			if (!isInBounds(cursor)) return true;
			
			OpaqueCube adjacent = getBlock(cursor);
			
			if (adjacent == null) return true;
			if (adjacent.isOpaque(face)) return false;
			
			return true;
			
		} finally {
			cursor.sub(face.getVector());
		}
	}

	private boolean isInBounds(Vec3i cursor) {
		return
				isInBounds(cursor.x) &&
				isInBounds(cursor.y) &&
				isInBounds(cursor.z);
	}

	private boolean isInBounds(int c) {
		return c >= 0 && c < BLOCKS_PER_CHUNK;
	}

}
