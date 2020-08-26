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
package ru.windcorp.progressia.common.world;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.block.BlockData;
import ru.windcorp.progressia.common.block.BlockDataRegistry;

public class ChunkData {
	
	public static final int BLOCKS_PER_CHUNK = 16;
	public static final int TILES_PER_FACE = 8;
	
	private final Vec3i position = new Vec3i();
	
	private final BlockData[] blocks = new BlockData[
		BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK
	];
	
	private final BlockData grass = BlockDataRegistry.get("Test:Grass");
	private final BlockData dirt = BlockDataRegistry.get("Test:Dirt");
	private final BlockData stone = BlockDataRegistry.get("Test:Stone");
	private final BlockData air = BlockDataRegistry.get("Test:Air");
	
	public ChunkData(int x, int y, int z) {
		this.position.set(x, y, z);
		
		tmp_generate();
	}
	
	private void tmp_generate() {
		Vec3i aPoint = new Vec3i(5, 0, BLOCKS_PER_CHUNK + BLOCKS_PER_CHUNK/2);
		Vec3i pos = new Vec3i();
		
		for (int x = 0; x < BLOCKS_PER_CHUNK; ++x) {
			for (int y = 0; y < BLOCKS_PER_CHUNK; ++y) {
				for (int z = 0; z < BLOCKS_PER_CHUNK; ++z) {
					
					pos.set(x, y, z);
					float f = aPoint.sub(pos, pos).length();
					pos.set(x, y, z);
					
					if (f > 17) {
						setBlock(pos, stone);
					} else if (f > 14) {
						setBlock(pos, dirt);
					} else {
						setBlock(pos, air);
					}
					
				}
			}
		}
		
		for (int x = 0; x < BLOCKS_PER_CHUNK; ++x) {
			for (int y = 0; y < BLOCKS_PER_CHUNK; ++y) {
				pos.set(x, y, 0);
				
				for (pos.z = BLOCKS_PER_CHUNK - 1; pos.z >= 0 && getBlock(pos) == air; --pos.z);
				
				setBlock(pos, grass);
			}
		}
	}

	public BlockData getBlock(Vec3i posInChunk) {
		if (!isInBounds(posInChunk)) {
			throw new IllegalArgumentException(
					"Coordinates " + str(posInChunk) + " "
							+ "are not legal chunk coordinates"
			);
		}
		
		return blocks[getBlockIndex(posInChunk)];
	}

	public void setBlock(Vec3i posInChunk, BlockData block) {
		if (!isInBounds(posInChunk)) {
			throw new IllegalArgumentException(
					"Coordinates " + str(posInChunk) + " "
							+ "are not legal chunk coordinates"
			);
		}
		
		blocks[getBlockIndex(posInChunk)] = block;
	}
	
	private boolean isInBounds(Vec3i posInChunk) {
		return
				posInChunk.x >= 0 && posInChunk.x < BLOCKS_PER_CHUNK &&
				posInChunk.y >= 0 && posInChunk.y < BLOCKS_PER_CHUNK &&
				posInChunk.z >= 0 && posInChunk.z < BLOCKS_PER_CHUNK;
	}
	
	private int getBlockIndex(Vec3i posInChunk) {
		return
				posInChunk.z * BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK +
				posInChunk.y * BLOCKS_PER_CHUNK +
				posInChunk.x;
	}
	
	public int getX() {
		return position.x;
	}
	
	public int getY() {
		return position.y;
	}
	
	public int getZ() {
		return position.z;
	}
	
	public Vec3i getPosition() {
		return position;
	}
	
	private static String str(Vec3i v) {
		return "(" + v.x + "; " + v.y + "; " + v.z + ")";
	}

}
