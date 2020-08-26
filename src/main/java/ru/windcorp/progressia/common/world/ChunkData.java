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

import static ru.windcorp.progressia.common.block.BlockFace.*;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Lists;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.block.BlockData;
import ru.windcorp.progressia.common.block.BlockDataRegistry;
import ru.windcorp.progressia.common.block.BlockFace;
import ru.windcorp.progressia.common.block.TileData;
import ru.windcorp.progressia.common.block.TileDataRegistry;
import ru.windcorp.progressia.common.util.SizeLimitedList;
import ru.windcorp.progressia.common.util.Vectors;

public class ChunkData {
	
	public static final int BLOCKS_PER_CHUNK = 16;
	public static final int TILES_PER_FACE = 8;
	
	private final Vec3i position = new Vec3i();
	private final WorldData world;
	
	private final BlockData[] blocks = new BlockData[
		BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK
	];
	
	@SuppressWarnings("unchecked")
	private final List<TileData>[] tiles = (List<TileData>[]) new List<?>[
		BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK *
		BLOCK_FACE_COUNT
	];
	
	public ChunkData(int x, int y, int z, WorldData world) {
		this.position.set(x, y, z);
		this.world = world;
		
		tmp_generate();
	}
	
	private void tmp_generate() {
		BlockData grass = BlockDataRegistry.get("Test:Grass");
		BlockData dirt = BlockDataRegistry.get("Test:Dirt");
		BlockData stone = BlockDataRegistry.get("Test:Stone");
		BlockData air = BlockDataRegistry.get("Test:Air");
		
		TileData stones = TileDataRegistry.get("Test:Stones");
	
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
				
				int hash = x*x * 13 ^ y*y * 37 ^ pos.z*pos.z * 129;
				if (hash % 5 == 0) {
					getTiles(pos, BlockFace.TOP).add(stones);
				}
			}
		}
	}

	public BlockData getBlock(Vec3i posInChunk) {
		return blocks[getBlockIndex(posInChunk)];
	}

	public void setBlock(Vec3i posInChunk, BlockData block) {
		blocks[getBlockIndex(posInChunk)] = block;
	}
	
	public List<TileData> getTilesOrNull(Vec3i blockInChunk, BlockFace face) {
		return tiles[getTileIndex(blockInChunk, face)];
	}
	
	/**
	 * Internal use only. Modify a list returned by
	 * {@link #getTiles(Vec3i, BlockFace)} or
	 * {@link #getTilesOrNull(Vec3i, BlockFace)}
	 * to change tiles.
	 */
	protected void setTiles(
			Vec3i blockInChunk, BlockFace face,
			List<TileData> tiles
	) {
		this.tiles[getTileIndex(blockInChunk, face)] = tiles;
	}
	
	public boolean hasTiles(Vec3i blockInChunk, BlockFace face) {
		return getTilesOrNull(blockInChunk, face) != null;
	}
	
	public List<TileData> getTiles(Vec3i blockInChunk, BlockFace face) {
		int index = getTileIndex(blockInChunk, face);
		
		if (tiles[index] == null) {
			createTileContainer(blockInChunk, face);
		}
		
		return tiles[index];
	}
	
	private void createTileContainer(Vec3i blockInChunk, BlockFace face) {
		if (isBorder(blockInChunk, face)) {
			createBorderTileContainer(blockInChunk, face);
		} else {
			createNormalTileContainer(blockInChunk, face);
		}
	}

	private void createNormalTileContainer(Vec3i blockInChunk, BlockFace face) {
		List<TileData> primaryList =
				SizeLimitedList.wrap(
						new ArrayList<>(TILES_PER_FACE), TILES_PER_FACE
				);
		
		List<TileData> secondaryList = Lists.reverse(primaryList);
		
		
		Vec3i cursor = Vectors.grab3i()
				.set(blockInChunk.x, blockInChunk.y, blockInChunk.z);
		
		face = face.getPrimaryAndMoveCursor(cursor);
		setTiles(cursor, face, primaryList);
		
		face = face.getSecondaryAndMoveCursor(cursor);
		setTiles(cursor, face, secondaryList);
	}

	private void createBorderTileContainer(Vec3i blockInChunk, BlockFace face) {
		// TODO cooperate with neighbours
		setTiles(
				blockInChunk, face,
				SizeLimitedList.wrap(
						new ArrayList<>(TILES_PER_FACE), TILES_PER_FACE
				)
		);
	}

	private static int getBlockIndex(Vec3i posInChunk) {
		checkLocalCoordinates(posInChunk);
		
		return
				posInChunk.z * BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK +
				posInChunk.y * BLOCKS_PER_CHUNK +
				posInChunk.x;
	}
	
	private static int getTileIndex(Vec3i posInChunk, BlockFace face) {
		return
				getBlockIndex(posInChunk) * BLOCK_FACE_COUNT +
				face.getId();
	}
	
	private static void checkLocalCoordinates(Vec3i posInChunk) {
		if (!isInBounds(posInChunk)) {
			throw new IllegalArgumentException(
					"Coordinates " + str(posInChunk) + " "
							+ "are not legal chunk coordinates"
			);
		}
	}
	
	private static boolean isInBounds(Vec3i posInChunk) {
		return
				posInChunk.x >= 0 && posInChunk.x < BLOCKS_PER_CHUNK &&
				posInChunk.y >= 0 && posInChunk.y < BLOCKS_PER_CHUNK &&
				posInChunk.z >= 0 && posInChunk.z < BLOCKS_PER_CHUNK;
	}

	private boolean isBorder(Vec3i blockInChunk, BlockFace face) {
		final int min = 0, max = BLOCKS_PER_CHUNK - 1;
		
		return
				(blockInChunk.x == min && face == SOUTH ) ||
				(blockInChunk.x == max && face == NORTH ) ||
				(blockInChunk.y == min && face == WEST  ) ||
				(blockInChunk.y == max && face == EAST  ) ||
				(blockInChunk.z == min && face == BOTTOM) ||
				(blockInChunk.z == max && face == TOP   );
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
	
	public WorldData getWorld() {
		return world;
	}
	
	private static String str(Vec3i v) {
		return "(" + v.x + "; " + v.y + "; " + v.z + ")";
	}

}
