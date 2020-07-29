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
package ru.windcorp.optica.common.world;

import glm.vec._3.i.Vec3i;
import ru.windcorp.optica.common.block.BlockData;
import ru.windcorp.optica.common.block.BlockDataRegistry;

public class ChunkData {
	
	public static final int BLOCKS_PER_CHUNK = 16;
	
	private final int x;
	private final int y;
	private final int z;
	
	private final BlockData[][][] blocks = new BlockData[BLOCKS_PER_CHUNK]
	                                                    [BLOCKS_PER_CHUNK]
	                                                    [BLOCKS_PER_CHUNK];
	
	private final BlockData grass = BlockDataRegistry.get("Test:Grass");
	private final BlockData dirt = BlockDataRegistry.get("Test:Dirt");
	private final BlockData stone = BlockDataRegistry.get("Test:Stone");
	private final BlockData air = BlockDataRegistry.get("Test:Air");
	
	public ChunkData(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
		
		tmp_generate();
	}
	
	public BlockData[][][] tmp_getBlocks() {
		return blocks;
	}
	
	private void tmp_generate() {
		Vec3i aPoint = new Vec3i(5, 0, BLOCKS_PER_CHUNK + BLOCKS_PER_CHUNK/2);
		Vec3i pos = new Vec3i();
		
		for (int x = 0; x < BLOCKS_PER_CHUNK; ++x) {
			for (int y = 0; y < BLOCKS_PER_CHUNK; ++y) {
				for (int z = 0; z < BLOCKS_PER_CHUNK; ++z) {
					
					pos.set(x, y, z);
					
					float f = aPoint.sub(pos, pos).length();
					
					if (f > 17) {
						blocks[x][y][z] = stone;
					} else if (f > 14) {
						blocks[x][y][z] = dirt;
					} else {
						blocks[x][y][z] = air;
					}
					
				}
			}
		}
		
		for (int x = 0; x < BLOCKS_PER_CHUNK; ++x) {
			for (int y = 0; y < BLOCKS_PER_CHUNK; ++y) {
				int z;
				for (z = BLOCKS_PER_CHUNK - 1; z >= 0 && blocks[x][y][z] == air; --z);
				
				blocks[x][y][z] = grass;
			}
		}
	}

	public BlockData getBlock(int xInChunk, int yInChunk, int zInChunk) {
		if (!isInBounds(xInChunk, yInChunk, zInChunk)) {
			throw new IllegalArgumentException(
					"Coordinates (" + x + "; " + y + "; " + z + ") "
							+ "are not legal chunk coordinates"
			);
		}
		
		return blocks[xInChunk][yInChunk][zInChunk];
	}
	
	private boolean isInBounds(int xInChunk, int yInChunk, int zInChunk) {
		return
				xInChunk >= 0 && xInChunk < BLOCKS_PER_CHUNK ||
				yInChunk >= 0 && yInChunk < BLOCKS_PER_CHUNK ||
				zInChunk >= 0 && zInChunk < BLOCKS_PER_CHUNK;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getZ() {
		return z;
	}

}
