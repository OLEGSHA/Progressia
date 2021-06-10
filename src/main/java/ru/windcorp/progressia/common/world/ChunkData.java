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

package ru.windcorp.progressia.common.world;

import static ru.windcorp.progressia.common.world.block.BlockFace.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.util.VectorUtil;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.block.BlockFace;
import ru.windcorp.progressia.common.world.generic.GenericChunk;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataStack;
import ru.windcorp.progressia.common.world.tile.TileReference;
import ru.windcorp.progressia.common.world.tile.TileStackIsFullException;

public class ChunkData implements GenericChunk<ChunkData, BlockData, TileData, TileDataStack> {

	public static final int BLOCKS_PER_CHUNK = Coordinates.CHUNK_SIZE;

	private final Vec3i position = new Vec3i();
	private final WorldData world;

	private final BlockData[] blocks = new BlockData[BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK];

	private final TileDataStack[] tiles = new TileDataStack[BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK
			* BLOCK_FACE_COUNT];

	private Object generationHint = null;

	private final Collection<ChunkDataListener> listeners = Collections.synchronizedCollection(new ArrayList<>());

	public ChunkData(Vec3i position, WorldData world) {
		this.position.set(position.x, position.y, position.z);
		this.world = world;
	}

	@Override
	public Vec3i getPosition() {
		return position;
	}

	@Override
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

	@Override
	public TileDataStack getTilesOrNull(Vec3i blockInChunk, BlockFace face) {
		return tiles[getTileIndex(blockInChunk, face)];
	}

	/**
	 * Internal use only. Modify a list returned by
	 * {@link #getTiles(Vec3i, BlockFace)} or
	 * {@link #getTilesOrNull(Vec3i, BlockFace)} to change tiles.
	 */
	protected void setTiles(Vec3i blockInChunk, BlockFace face, TileDataStack tiles) {
		this.tiles[getTileIndex(blockInChunk, face)] = tiles;
	}

	@Override
	public boolean hasTiles(Vec3i blockInChunk, BlockFace face) {
		return getTilesOrNull(blockInChunk, face) != null;
	}

	@Override
	public TileDataStack getTiles(Vec3i blockInChunk, BlockFace face) {
		int index = getTileIndex(blockInChunk, face);

		if (tiles[index] == null) {
			createTileStack(blockInChunk, face);
		}

		return tiles[index];
	}

	private void createTileStack(Vec3i blockInChunk, BlockFace face) {
		Vec3i independentBlockInChunk = conjureIndependentBlockInChunkVec3i(blockInChunk);
		TileDataStackImpl stack = new TileDataStackImpl(independentBlockInChunk, face);
		setTiles(blockInChunk, face, stack);
	}

	private Vec3i conjureIndependentBlockInChunkVec3i(Vec3i blockInChunk) {
		for (int i = 0; i < BlockFace.BLOCK_FACE_COUNT; ++i) {
			TileDataStack stack = getTilesOrNull(blockInChunk, BlockFace.getFaces().get(i));
			if (stack instanceof TileDataStackImpl) {
				return ((TileDataStackImpl) stack).blockInChunk;
			}
		}

		return new Vec3i(blockInChunk);
	}

	private static int getBlockIndex(Vec3i posInChunk) {
		checkLocalCoordinates(posInChunk);

		return posInChunk.z * BLOCKS_PER_CHUNK * BLOCKS_PER_CHUNK + posInChunk.y * BLOCKS_PER_CHUNK + posInChunk.x;
	}

	private static int getTileIndex(Vec3i posInChunk, BlockFace face) {
		return getBlockIndex(posInChunk) * BLOCK_FACE_COUNT + face.getId();
	}

	private static void checkLocalCoordinates(Vec3i posInChunk) {
		if (!isInBounds(posInChunk)) {
			throw new IllegalCoordinatesException(
					"Coordinates " + str(posInChunk) + " " + "are not legal chunk coordinates");
		}
	}

	public static boolean isInBounds(Vec3i posInChunk) {
		return posInChunk.x >= 0 && posInChunk.x < BLOCKS_PER_CHUNK && posInChunk.y >= 0
				&& posInChunk.y < BLOCKS_PER_CHUNK && posInChunk.z >= 0 && posInChunk.z < BLOCKS_PER_CHUNK;
	}

	public boolean isBorder(Vec3i blockInChunk, BlockFace face) {
		final int min = 0, max = BLOCKS_PER_CHUNK - 1;
		return (blockInChunk.x == min && face == SOUTH) || (blockInChunk.x == max && face == NORTH)
				|| (blockInChunk.y == min && face == EAST) || (blockInChunk.y == max && face == WEST)
				|| (blockInChunk.z == min && face == BOTTOM) || (blockInChunk.z == max && face == TOP);
	}

	public void forEachBlock(Consumer<Vec3i> action) {
		VectorUtil.iterateCuboid(0, 0, 0, BLOCKS_PER_CHUNK, BLOCKS_PER_CHUNK, BLOCKS_PER_CHUNK, action);
	}

	public void forEachTileStack(Consumer<TileDataStack> action) {
		forEachBlock(blockInChunk -> {
			for (BlockFace face : BlockFace.getFaces()) {
				TileDataStack stack = getTilesOrNull(blockInChunk, face);
				if (stack == null)
					continue;
				action.accept(stack);
			}
		});
	}

	/**
	 * Iterates over all tiles in this chunk.
	 * 
	 * @param action
	 *            the action to perform. {@code TileLocation} refers to each
	 *            tile using its primary block
	 */
	public void forEachTile(BiConsumer<TileDataStack, TileData> action) {
		forEachTileStack(stack -> stack.forEach(tileData -> action.accept(stack, tileData)));
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

	public Object getGenerationHint() {
		return generationHint;
	}

	public void setGenerationHint(Object generationHint) {
		this.generationHint = generationHint;
	}

	/**
	 * Implementation of {@link TileDataStack} used internally by
	 * {@link ChunkData} to actually store the tiles. This is basically an array
	 * wrapper with reporting capabilities.
	 * 
	 * @author javapony
	 */
	private class TileDataStackImpl extends TileDataStack {
		private class TileReferenceImpl implements TileReference {
			private int index;

			public TileReferenceImpl(int index) {
				this.index = index;
			}

			public void incrementIndex() {
				this.index++;
			}

			public void decrementIndex() {
				this.index--;
			}

			public void invalidate() {
				this.index = 0;
			}

			@Override
			public TileData get() {
				if (!isValid())
					return null;
				return TileDataStackImpl.this.get(this.index);
			}

			@Override
			public int getIndex() {
				return index;
			}

			@Override
			public TileDataStack getStack() {
				return TileDataStackImpl.this;
			}

			@Override
			public boolean isValid() {
				return this.index >= 0;
			}
		}

		private final TileData[] tiles = new TileData[TILES_PER_FACE];
		private int size = 0;

		private final TileReferenceImpl[] references = new TileReferenceImpl[tiles.length];
		private final int[] indicesByTag = new int[tiles.length];
		private final int[] tagsByIndex = new int[tiles.length];

		{
			Arrays.fill(indicesByTag, -1);
			Arrays.fill(tagsByIndex, -1);
		}

		/*
		 * Potentially shared
		 */
		private final Vec3i blockInChunk;
		private final BlockFace face;

		public TileDataStackImpl(Vec3i blockInChunk, BlockFace face) {
			this.blockInChunk = blockInChunk;
			this.face = face;
		}

		@Override
		public Vec3i getBlockInChunk(Vec3i output) {
			if (output == null)
				output = new Vec3i();
			output.set(blockInChunk.x, blockInChunk.y, blockInChunk.z);
			return output;
		}

		@Override
		public BlockFace getFace() {
			return face;
		}

		@Override
		public ChunkData getChunk() {
			return ChunkData.this;
		}

		@Override
		public int size() {
			return size;
		}

		@Override
		public TileData get(int index) {
			checkIndex(index, false);

			return tiles[index];
		}

		@Override
		public TileData set(int index, TileData tile) {
			Objects.requireNonNull(tile, "tile");
			TileData previous = get(index); // checks index

			tiles[index] = tile;

			if (references[index] != null) {
				references[index].invalidate();
				references[index] = null;
			}

			assert checkConsistency();

			report(previous, tile);
			return previous;
		}

		@Override
		public void add(int index, TileData tile) {
			Objects.requireNonNull(tile, "tile");
			checkIndex(index, true);

			if (index != size()) {
				System.arraycopy(tiles, index + 1, tiles, index + 2, size - index);

				for (int i = index; i < size; ++i) {
					if (references[i] != null) {
						references[i].incrementIndex();
					}

					indicesByTag[tagsByIndex[i]]++;
				}

				System.arraycopy(references, index + 1, references, index + 2, size - index);
				System.arraycopy(tagsByIndex, index + 1, tagsByIndex, index + 2, size - index);
			}

			size++;
			tiles[index] = tile;
			references[index] = null;

			for (int tag = 0; tag < indicesByTag.length; ++tag) {
				if (tagsByIndex[tag] == -1) {
					indicesByTag[tag] = index;
					tagsByIndex[index] = tag;
					break;
				}
			}

			modCount++;
			assert checkConsistency();

			report(null, tile);
		}

		@Override
		public void load(TileData tile, int tag) {
			addFarthest(tile);

			int assignedTag = getIndexByTag(tag);

			if (assignedTag == tag)
				return;
			if (assignedTag == -1) {
				throw new IllegalArgumentException(
						"Tag " + tag + " already used by tile at index " + getIndexByTag(tag));
			}

			indicesByTag[tagsByIndex[size() - 1]] = -1;
			tagsByIndex[size() - 1] = tag;
			indicesByTag[tag] = size() - 1;

			assert checkConsistency();
		}

		@Override
		public TileData remove(int index) {
			TileData previous = get(index); // checks index

			if (references[index] != null) {
				references[index].invalidate();
			}

			indicesByTag[tagsByIndex[index]] = -1;

			if (index != size() - 1) {
				System.arraycopy(tiles, index + 1, tiles, index, size - index - 1);

				for (int i = index + 1; i < size; ++i) {
					if (references[i] != null) {
						references[i].decrementIndex();
					}

					indicesByTag[tagsByIndex[i]]--;
				}

				System.arraycopy(references, index + 1, references, index, size - index - 1);
				System.arraycopy(tagsByIndex, index + 1, tagsByIndex, index, size - index - 1);
			}

			size--;
			tiles[size] = null;
			references[size] = null;
			tagsByIndex[size] = -1;

			modCount++;
			assert checkConsistency();

			report(previous, null);
			return previous;
		}

		@Override
		public TileReference getReference(int index) {
			checkIndex(index, false);

			if (references[index] == null) {
				references[index] = new TileReferenceImpl(index);
			}

			return references[index];
		}

		@Override
		public int getIndexByTag(int tag) {
			return indicesByTag[tag];
		}

		@Override
		public int getTagByIndex(int index) {
			checkIndex(index, false);
			return tagsByIndex[index];
		}

		@Override
		public void clear() {
			while (!isEmpty()) {
				removeFarthest();
			}
		}

		private void checkIndex(int index, boolean isSizeAllowed) {
			if (isSizeAllowed ? (index > size()) : (index >= size()))
				throw new IndexOutOfBoundsException("Index " + index + " is out of bounds: size is " + size);

			if (index < 0)
				throw new IndexOutOfBoundsException("Index " + index + " is out of bounds: index cannot be negative");

			if (index >= TILES_PER_FACE)
				throw new TileStackIsFullException(
						"Index " + index + " is out of bounds: maximum tile stack size is " + TILES_PER_FACE);
		}

		private void report(TileData previous, TileData current) {
			ChunkData.this.getListeners().forEach(l -> {
				if (previous != null) {
					l.onChunkTilesChanged(ChunkData.this, blockInChunk, face, previous, false);
				}

				if (current != null) {
					l.onChunkTilesChanged(ChunkData.this, blockInChunk, face, current, true);
				}

				l.onChunkChanged(ChunkData.this);
			});
		}

		private boolean checkConsistency() {
			int index;

			for (index = 0; index < size(); ++index) {
				if (get(index) == null)
					throw new AssertionError("get(index) is null");

				if (references[index] != null) {
					TileReference ref = getReference(index);
					if (ref == null)
						throw new AssertionError("references[index] is not null but getReference(index) is");
					if (!ref.isValid())
						throw new AssertionError("Reference is not valid");
					if (ref.get() != get(index))
						throw new AssertionError("Reference points to " + (ref.get() == null ? "null" : "wrong tile"));
					if (ref.getIndex() != index)
						throw new AssertionError("Reference has invalid index");
					if (ref.getStack() != this)
						throw new AssertionError("Reference has invalid TDS");
				}

				if (index != indicesByTag[tagsByIndex[index]])
					throw new AssertionError("Tag mapping is inconsistent");
				if (index != getIndexByTag(getTagByIndex(index)))
					throw new AssertionError("Tag methods are inconsistent with tag mapping");
			}

			for (; index < tiles.length; ++index) {
				if (tiles[index] != null)
					throw new AssertionError("Leftover tile detected");
				if (references[index] != null)
					throw new AssertionError("Leftover reference detected");
				if (tagsByIndex[index] != -1)
					throw new AssertionError("Leftover tags detected");
			}

			return true;
		}

	}

}
