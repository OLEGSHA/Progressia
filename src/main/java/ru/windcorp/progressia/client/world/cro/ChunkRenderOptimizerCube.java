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
package ru.windcorp.progressia.client.world.cro;

import static ru.windcorp.progressia.common.world.ChunkData.BLOCKS_PER_CHUNK;
import static ru.windcorp.progressia.common.world.block.BlockFace.BLOCK_FACE_COUNT;
import static ru.windcorp.progressia.common.world.generic.GenericTileStack.TILES_PER_FACE;

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
import ru.windcorp.progressia.client.world.block.BlockRender;
import ru.windcorp.progressia.client.world.tile.TileRender;
import ru.windcorp.progressia.common.world.block.BlockFace;

public class ChunkRenderOptimizerCube extends ChunkRenderOptimizer {
	
	public static interface OpaqueCube {
		public Texture getTexture(BlockFace face);
		public boolean isOpaque(BlockFace face);
		public boolean isBlockOpaque();
	}
	
	public static interface OpaqueTile {
		public Texture getTexture(BlockFace face);
		public boolean isOpaque(BlockFace face);
	}
	
	private static class BlockInfo {
		OpaqueCube block;
		final FaceInfo[] faces = new FaceInfo[BLOCK_FACE_COUNT];
		
		{
			for (int i = 0; i < faces.length; ++i) {
				faces[i] = new FaceInfo();
			}
		}
	}
	
	private static class FaceInfo {
		static final int NO_OPAQUE_TILES = -1;
		
		int topOpaqueTile = NO_OPAQUE_TILES;
		final OpaqueTile[] tiles = new OpaqueTile[TILES_PER_FACE];
		int tileCount = 0;
	}
	
	private static final Vec3 COLOR_MULTIPLIER = new Vec3(1, 1, 1);
	
	private final BlockInfo[][][] data = 
			new BlockInfo[BLOCKS_PER_CHUNK]
			             [BLOCKS_PER_CHUNK]
			             [BLOCKS_PER_CHUNK];
	
	{
		for (int x = 0; x < BLOCKS_PER_CHUNK; ++x) {
			for (int y = 0; y < BLOCKS_PER_CHUNK; ++y) {
				for (int z = 0; z < BLOCKS_PER_CHUNK; ++z) {
					data[x][y][z] = new BlockInfo();
				}
			}
		}
	}

	@Override
	public void startRender(ChunkRender chunk) {
		// Do nothing
	}

	@Override
	public void processBlock(BlockRender block, Vec3i pos) {
		if (!(block instanceof OpaqueCube)) return;
		OpaqueCube opaqueCube = (OpaqueCube) block;
		addBlock(pos, opaqueCube);
	}
	
	@Override
	public void processTile(TileRender tile, Vec3i pos, BlockFace face) {
		if (!(tile instanceof OpaqueTile)) return;
		OpaqueTile opaqueTile = (OpaqueTile) tile;
		addTile(pos, face, opaqueTile);
	}

	protected void addBlock(Vec3i pos, OpaqueCube cube) {
		getBlock(pos).block = cube;
	}
	
	private void addTile(Vec3i pos, BlockFace face, OpaqueTile opaqueTile) {
		FaceInfo faceInfo = getFace(pos, face);
		
		int index = faceInfo.tileCount;
		faceInfo.tileCount++;
		
		faceInfo.tiles[index] = opaqueTile;
		
		if (opaqueTile.isOpaque(face)) {
			faceInfo.topOpaqueTile = index;
		}
	}
	
	protected BlockInfo getBlock(Vec3i cursor) {
		return data[cursor.x][cursor.y][cursor.z];
	}
	
	protected FaceInfo getFace(Vec3i cursor, BlockFace face) {
		return getBlock(cursor).faces[face.getId()];
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
					processInnerFaces(cursor, shapeFaces::add);
					processOuterFaces(cursor, shapeFaces::add);
				}
			}
		}
		
		return new Shape(
				Usage.STATIC,
				WorldRenderProgram.getDefault(),
				shapeFaces.toArray(new Face[shapeFaces.size()])
		);
	}
	
	private void processOuterFaces(
			Vec3i cursor,
			Consumer<Face> output
	) {
		for (BlockFace face : BlockFace.getFaces()) {
			if (!shouldRenderOuterFace(cursor, face)) continue;
			
			Vec3 faceOrigin = new Vec3(cursor.x, cursor.y, cursor.z);
			Vec3 offset = new Vec3(face.getVector().x, face.getVector().y, face.getVector().z).mul(1f / 128);
			
			FaceInfo info = getFace(cursor, face);
			
			if (info.topOpaqueTile == FaceInfo.NO_OPAQUE_TILES) {
				OpaqueCube block = getBlock(cursor).block;
				
				if (block != null) {
					addFace(
							faceOrigin, face,
							getBlock(cursor).block.getTexture(face), 
							output
					);
					
					faceOrigin.add(offset);
				}
			}
			
			int startLayer = info.topOpaqueTile;
			if (startLayer == FaceInfo.NO_OPAQUE_TILES) {
				startLayer = 0;
			}
			
			for (int layer = startLayer; layer < info.tileCount; ++layer) {
				addFace(
						faceOrigin, face,
						info.tiles[layer].getTexture(face),
						output
				);
				
				faceOrigin.add(offset);
			}
		}
	}
	
	private void addFace(
			Vec3 cursor, BlockFace face,
			Texture texture,
			Consumer<Face> output
	) {
		if (texture == null) return;
		
		output.accept(Faces.createBlockFace(
				WorldRenderProgram.getDefault(),
				texture,
				COLOR_MULTIPLIER,
				new Vec3(cursor),
				face,
				false
		));
	}

	private boolean shouldRenderOuterFace(Vec3i cursor, BlockFace face) {
		cursor.add(face.getVector());
		try {
			
			// TODO handle neighboring chunks properly
			if (!isInBounds(cursor)) return true;
			
			OpaqueCube adjacent = getBlock(cursor).block;
			
			if (adjacent == null) return true;
			if (adjacent.isOpaque(face)) return false;
			
			return true;
			
		} finally {
			cursor.sub(face.getVector());
		}
	}

	private void processInnerFaces(
			Vec3i cursor,
			Consumer<Face> output
	) {
//		if (block.isBlockOpaque()) return;
//		
//		for (BlockFace face : BlockFace.getFaces()) {
//			
//			Texture texture = block.getTexture(face);
//			if (texture == null) continue;
//			
//			output.accept(Faces.createBlockFace(
//					WorldRenderProgram.getDefault(),
//					texture,
//					COLOR_MULTIPLIER,
//					new Vec3(cursor.x, cursor.y, cursor.z),
//					face,
//					true
//			));
//			
//		}
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
