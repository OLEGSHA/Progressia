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

import static ru.windcorp.progressia.common.world.block.BlockFace.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.google.common.collect.Lists;

import glm.vec._2.Vec2;
import glm.vec._3.Vec3;
import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.client.world.tile.TileLocation;
import ru.windcorp.progressia.common.util.SizeLimitedList;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.util.Vectors;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockDataRegistry;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.entity.EntityData;
import ru.windcorp.progressia.common.world.entity.EntityDataRegistry;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataRegistry;

public class ChunkData {
	
	public static final int BLOCKS_PER_CHUNK = Coordinates.CHUNK_SIZE;
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
	
	private final List<EntityData> entities =
			Collections.synchronizedList(new ArrayList<>());
	
	private final Collection<ChunkDataListener> listeners =
			Collections.synchronizedCollection(new ArrayList<>());
	
	public ChunkData(Vec3i position, WorldData world) {
		this.position.set(position.x, position.y, position.z);
		this.world = world;
		
		tmp_generate();
	}
	
	private void tmp_generate() {
		BlockData dirt = BlockDataRegistry.getInstance().get("Test:Dirt");
		BlockData stone = BlockDataRegistry.getInstance().get("Test:Stone");
		BlockData air = BlockDataRegistry.getInstance().get("Test:Air");

		TileData grass = TileDataRegistry.getInstance().get("Test:Grass");
		TileData stones = TileDataRegistry.getInstance().get("Test:Stones");
		TileData flowers = TileDataRegistry.getInstance().get("Test:YellowFlowers");
		TileData sand = TileDataRegistry.getInstance().get("Test:Sand");
	
		Vec3i aPoint = new Vec3i(5, 0, BLOCKS_PER_CHUNK + BLOCKS_PER_CHUNK/2).sub(getPosition());
		Vec3i pos = new Vec3i();
		
		for (int x = 0; x < BLOCKS_PER_CHUNK; ++x) {
			for (int y = 0; y < BLOCKS_PER_CHUNK; ++y) {
				for (int z = 0; z < BLOCKS_PER_CHUNK; ++z) {
					
					pos.set(x, y, z);
					float f = aPoint.sub(pos, pos).length();
					pos.set(x, y, z);
					
					if (f > 17) {
						setBlock(pos, stone, false);
					} else if (f > 14) {
						setBlock(pos, dirt, false);
					} else {
						setBlock(pos, air, false);
					}
					
				}
			}
		}
		
		for (int x = 0; x < BLOCKS_PER_CHUNK; ++x) {
			for (int y = 0; y < BLOCKS_PER_CHUNK; ++y) {
				pos.set(x, y, 0);
				
				for (pos.z = BLOCKS_PER_CHUNK - 1; pos.z >= 0 && getBlock(pos) == air; --pos.z);
				
				getTiles(pos, BlockFace.TOP).add(grass);
				for (BlockFace face : BlockFace.getFaces()) {
					if (face.getVector().z != 0) continue;
					getTiles(pos, face).add(grass);
				}
				
				int hash = x*x * 19 ^ y*y * 41 ^ pos.z*pos.z * 147;
				if (hash % 5 == 0) {
					getTiles(pos, BlockFace.TOP).add(sand);
				}
				
				hash = x*x * 13 ^ y*y * 37 ^ pos.z*pos.z * 129;
				if (hash % 5 == 0) {
					getTiles(pos, BlockFace.TOP).add(stones);
				}
				
				hash = x*x * 17 ^ y*y * 39 ^ pos.z*pos.z * 131;
				if (hash % 9 == 0) {
					getTiles(pos, BlockFace.TOP).add(flowers);
				}
			}
		}
		
		if (!getPosition().any()) {
			EntityData player = EntityDataRegistry.getInstance().create("Test:Player");
			player.setEntityId(0x42);
			player.setPosition(new Vec3(-6, -6, 20));
			player.setDirection(new Vec2(
					(float) Math.toRadians(40), (float) Math.toRadians(45)
			));
			getEntities().add(player);
			
			EntityData statie = EntityDataRegistry.getInstance().create("Test:Statie");
			statie.setEntityId(0xDEADBEEF);
			statie.setPosition(new Vec3(0, 15, 16));
			getEntities().add(statie);
		}
	}

	public BlockData getBlock(Vec3i posInChunk) {
		return blocks[getBlockIndex(posInChunk)];
	}

	public void setBlock(Vec3i posInChunk, BlockData block, boolean notify) {
		BlockData previous = blocks[getBlockIndex(posInChunk)];
		blocks[getBlockIndex(posInChunk)] = block;
		
		if (notify) {
			getListeners().forEach(l -> {
				l.onChunkBlockChanged(this, posInChunk, previous, block);
				l.onChunkChanged(this);
			});
		}
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
	
	public List<EntityData> getEntities() {
		return entities;
	}
	
	private static void checkLocalCoordinates(Vec3i posInChunk) {
		if (!isInBounds(posInChunk)) {
			throw new IllegalCoordinatesException(
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
				(blockInChunk.y == min && face == EAST  ) ||
				(blockInChunk.y == max && face == WEST  ) ||
				(blockInChunk.z == min && face == BOTTOM) ||
				(blockInChunk.z == max && face == TOP   );
	}
	
	public void forEachBlock(Consumer<Vec3i> action) {
		VectorUtil.forEachVectorInCuboid(
				0, 0, 0,
				BLOCKS_PER_CHUNK, BLOCKS_PER_CHUNK, BLOCKS_PER_CHUNK,
				action
		);
	}
	
	/**
	 * Iterates over all tiles in this chunk. Tiles are referenced using their
	 * primary block (so that the face is
	 * {@linkplain BlockFace#isPrimary() primary}).
	 * 
	 * @param action the action to perform. {@code TileLocation} refers to each
	 * tile using its primary block
	 */
	public void forEachTile(BiConsumer<TileLocation, TileData> action) {
		TileLocation loc = new TileLocation();
		
		forEachBlock(blockInChunk -> {
			loc.pos.set(blockInChunk.x, blockInChunk.y, blockInChunk.z);
			
			for (BlockFace face : BlockFace.getPrimaryFaces()) {
				List<TileData> list = getTilesOrNull(blockInChunk, face);
				if (list == null) continue;
				
				loc.face = face;
				
				for (loc.layer = 0; loc.layer < list.size(); ++loc.layer) {
					TileData tile = list.get(loc.layer);
					action.accept(loc, tile);
				}
			}
		});
	}
	
	public void forEachEntity(Consumer<EntityData> action) {
		getEntities().forEach(action);
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
	
	public Collection<ChunkDataListener> getListeners() {
		return listeners;
	}
	
	public void addListener(ChunkDataListener listener) {
		this.listeners.add(listener);
	}
	
	public void removeListener(ChunkDataListener listener) {
		this.listeners.remove(listener);
	}
	
	private static String str(Vec3i v) {
		return "(" + v.x + "; " + v.y + "; " + v.z + ")";
	}

	protected void onLoaded() {
		getListeners().forEach(l -> l.onChunkLoaded(this));
	}
	
	protected void beforeUnloaded() {
		getListeners().forEach(l -> l.beforeChunkUnloaded(this));
	}

}
