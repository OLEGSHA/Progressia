package ru.windcorp.progressia.common.world.tile;

import ru.windcorp.progressia.common.world.ChunkData;
import ru.windcorp.progressia.common.world.block.BlockData;
import ru.windcorp.progressia.common.world.generic.GenericTileStack;

public abstract class TileDataStack
extends GenericTileStack<
	TileDataStack,
	TileData,
	ChunkData
> {
	
	/**
	 * Inserts the specified tile at the specified position in this stack.
	 * Shifts the tile currently at that position (if any) and any tiles above to
	 * the top (adds one to their indices).
	 * @param index index at which the specified tile is to be inserted
	 * @param tile tile to be inserted
	 * @throws TileStackIsFullException if this stack is {@linkplain #isFull() full}
	 */
	/*
	 * Impl note: AbstractList provides a useless implementation of this method,
	 * make sure to override it in subclass
	 */
	@Override
	public abstract void add(int index, TileData tile);

	/**
	 * Replaces the tile at the specified position in this stack with the specified tile.
	 * @param index index of the tile to replace
	 * @param tile tile to be stored at the specified position
	 * @return the tile previously at the specified position
	 */
	/*
	 * Impl note: AbstractList provides a useless implementation of this method,
	 * make sure to override it in subclass
	 */
	@Override
	public abstract TileData set(int index, TileData tile);

	/**
	 * Removes the tile at the specified position in this list. Shifts any subsequent tiles
	 * to the left (subtracts one from their indices). Returns the tile that was removed
	 * from the list.
	 * @param index the index of the tile to be removed
	 * @return the tile previously at the specified position
	 */
	/*
	 * Impl note: AbstractList provides a useless implementation of this method,
	 * make sure to override it in subclass
	 */
	@Override
	public abstract TileData remove(int index);
	
	public abstract TileReference getReference(int index);
	
	public abstract int getIndexByTag(int tag);
	
	public abstract int getTagByIndex(int index);
	
	/*
	 * Aliases and overloads
	 */
	
	public void addClosest(TileData tile) {
		add(0, tile);
	}
	
	public void addFarthest(TileData tile) {
		add(size(), tile);
	}
	
	/**
	 * Attempts to {@link #add(int, TileData) add} the provided {@code tile}
	 * at {@code index}. If the stack is {@linkplain #isFull() full}, does nothing.
	 * @param index the index to insert the tile at
	 * @param tile the tile to try to add
	 * @return {@code true} iff this stack has changed
	 */
	public boolean offer(int index, TileData tile) {
		if (isFull()) return false;
		add(index, tile);
		return true;
	}
	
	public boolean offerClosest(TileData tile) {
		return offer(0, tile);
	}
	
	public boolean offerFarthest(TileData tile) {
		return offer(size(), tile);
	}
	
	public TileData removeClosest() {
		return remove(0);
	}
	
	public TileData removeFarthest() {
		return remove(size() - 1);
	}
	
	public TileData poll(int index) {
		if (size() <= index) return null;
		return remove(index);
	}
	
	public TileData pollClosest() {
		return poll(0);
	}
	
	public TileData pollFarthest() {
		return poll(size() - 1);
	}
	
	@Override
	public boolean add(TileData tile) {
		addFarthest(tile);
		return true;
	}
	
	public BlockData getHost() {
		return getChunk().getBlock(getBlockInChunk(null));
	}
	
}
