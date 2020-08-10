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
package ru.windcorp.progressia.client.world.renders.bro;

import static ru.windcorp.progressia.common.world.ChunkData.BLOCKS_PER_CHUNK;

import java.util.ArrayList;
import java.util.Collection;

import glm.vec._3.Vec3;
import ru.windcorp.progressia.client.graphics.backend.Usage;
import ru.windcorp.progressia.client.graphics.model.Face;
import ru.windcorp.progressia.client.graphics.model.Faces;
import ru.windcorp.progressia.client.graphics.model.Shape;
import ru.windcorp.progressia.client.graphics.texture.Texture;
import ru.windcorp.progressia.client.graphics.world.WorldRenderProgram;
import ru.windcorp.progressia.client.world.ChunkRender;
import ru.windcorp.progressia.client.world.renders.BlockRender;
import ru.windcorp.progressia.common.block.BlockFace;
import ru.windcorp.progressia.common.world.ChunkData;

public class BlockRenderOpaqueCubeOptimizer extends BlockRenderOptimizer {
	
	private static final int BLOCK_MASK = 1 << 7;
	
	private static final BlockFace[] GOOD_FACES = new BlockFace[] {
			BlockFace.TOP, BlockFace.NORTH, BlockFace.WEST
	};
	
	private static final Vec3 COLOR_MULTIPLIER = new Vec3(1, 1, 1);
	
	private final byte[][][] data = new byte[BLOCKS_PER_CHUNK + 1]
	                                        [BLOCKS_PER_CHUNK + 1]
	                                        [BLOCKS_PER_CHUNK + 1];
	
	private ChunkRender chunk;
	
	private final Vec3 blockCenter = new Vec3();

	@Override
	public void startRender(ChunkRender chunk) {
		this.chunk = chunk;
	}

	@Override
	public void processBlock(BlockRender block, int x, int y, int z) {
		addFace(x, y, z, BlockFace.TOP);
		addFace(x, y, z, BlockFace.BOTTOM);
		addFace(x, y, z, BlockFace.NORTH);
		addFace(x, y, z, BlockFace.SOUTH);
		addFace(x, y, z, BlockFace.EAST);
		addFace(x, y, z, BlockFace.WEST);
		addBlock(x, y, z);
	}
	
	protected void addFace(int x, int y, int z, BlockFace face) {
		switch (face) {
		case BOTTOM:
			z -= 1;
			face = BlockFace.TOP;
			break;
		case SOUTH:
			x -= 1;
			face = BlockFace.NORTH;
			break;
		case EAST:
			y -= 1;
			face = BlockFace.WEST;
			break;
		default:
		}
		
		data[x + 1][y + 1][z + 1] ^= 1 << face.ordinal();
	}
	
	protected void addBlock(int x, int y, int z) {
		data[x + 1][y + 1][z + 1] |= BLOCK_MASK;
	}
	
	protected boolean hasFace(int x, int y, int z, BlockFace face) {
		switch (face) {
		case BOTTOM:
			z -= 1;
			face = BlockFace.TOP;
			break;
		case SOUTH:
			x -= 1;
			face = BlockFace.NORTH;
			break;
		case EAST:
			y -= 1;
			face = BlockFace.WEST;
			break;
		default:
		}
		
		return (data[x + 1][y + 1][z + 1] & 1 << face.ordinal()) != 0;
	}
	
	protected boolean hasBlock(int x, int y, int z) {
		return (data[x + 1][y + 1][z + 1] & BLOCK_MASK) != 0;
	}

	@Override
	public Shape endRender() {
		
		Collection<Face> shapeFaces = new ArrayList<>(
				BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK * 3 +
				BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK * 3
		);
		
		for (int x = -1; x < ChunkData.BLOCKS_PER_CHUNK; ++x) {
			for (int y = -1; y < ChunkData.BLOCKS_PER_CHUNK; ++y) {
				for (int z = -1; z < ChunkData.BLOCKS_PER_CHUNK; ++z) {
					for (BlockFace face : GOOD_FACES) {
						
						if (!hasFace(x, y, z, face)) continue;
						
						Face shapeFace = null;
						
						if (!hasBlock(x, y, z)) {
							switch (face) {
							case TOP:
								shapeFace = createFace(
										x, y, z + 1,
										BlockFace.BOTTOM
								);
								break;
							case NORTH:
								shapeFace = createFace(
										x + 1, y, z,
										BlockFace.SOUTH
								);
								break;
							case WEST:
								shapeFace = createFace(
										x, y + 1, z,
										BlockFace.EAST
								);
								break;
							default:
							}
						} else {
							shapeFace = createFace(x, y, z, face);
						}
						
						shapeFaces.add(shapeFace);
						
					}
				}
			}
		}
		
		return new Shape(
				Usage.STATIC,
				WorldRenderProgram.getDefault(),
				shapeFaces.toArray(new Face[shapeFaces.size()])
		);
	}

	private Face createFace(int x, int y, int z, BlockFace face) {
		BlockRenderOpaqueCube blockRender =
				(BlockRenderOpaqueCube) chunk.getBlock(x, y, z);
		Texture texture = blockRender.getTexture(face);
		
		return Faces.createBlockFace(
				WorldRenderProgram.getDefault(),
				texture,
				COLOR_MULTIPLIER,
				blockCenter.set(x, y, z),
				face
		);
	}

}
