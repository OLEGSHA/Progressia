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

package ru.windcorp.progressia.common.world.generic;

import java.util.List;
import java.util.RandomAccess;

// @formatter:off
public interface TileGenericStackWO<
	B  extends BlockGeneric,
	T  extends TileGeneric,
	TS extends TileGenericStackWO     <B, T, TS, TR, C>,
	TR extends TileGenericReferenceWO <B, T, TS, TR, C>,
	C  extends ChunkGenericWO         <B, T, TS, TR, C>
>
	extends List<T>, RandomAccess {
// @formatter:on

	/**
	 * Inserts the specified tile at the specified position in this stack.
	 * Shifts the tile currently at that position (if any) and any tiles above
	 * to
	 * the top (adds one to their indices).
	 * 
	 * @param index index at which the specified tile is to be inserted
	 * @param tile  tile to be inserted
	 * @throws TileStackIsFullException if this stack is {@linkplain #isFull()
	 *                                  full}
	 */
	/*
	 * Impl note: AbstractList provides a useless implementation of this method,
	 * make sure to override it in subclass
	 */
	@Override
	void add(int index, T tile);

	/**
	 * Adds the specified tile at the end of this stack assigning it the
	 * provided tag.
	 * This method is useful for copying stacks when preserving tags is
	 * necessary.
	 * 
	 * @param tile the tile to add
	 * @param tag  the tag to assign the new tile
	 * @throws IllegalArgumentException if this stack already contains a tile
	 *                                  with the
	 *                                  provided tag
	 */
	void load(T tile, int tag);

	/**
	 * Replaces the tile at the specified position in this stack with the
	 * specified tile.
	 * 
	 * @param index index of the tile to replace
	 * @param tile  tile to be stored at the specified position
	 * @return the tile previously at the specified position
	 */
	/*
	 * Impl note: AbstractList provides a useless implementation of this method,
	 * make sure to override it in subclass
	 */
	@Override
	T set(int index, T tile);

	/**
	 * Removes the tile at the specified position in this list. Shifts any
	 * subsequent tiles
	 * to the left (subtracts one from their indices). Returns the tile that was
	 * removed
	 * from the list.
	 * 
	 * @param index the index of the tile to be removed
	 * @return the tile previously at the specified position
	 */
	/*
	 * Impl note: AbstractList provides a useless implementation of this method,
	 * make sure to override it in subclass
	 */
	@Override
	T remove(int index);

	/*
	 * Aliases and overloads
	 */

	default void addClosest(T tile) {
		add(0, tile);
	}

	default void addFarthest(T tile) {
		add(size(), tile);
	}
	
	boolean isFull();

	/**
	 * Attempts to {@link #add(int, TileData) add} the provided {@code tile}
	 * at {@code index}. If the stack is {@linkplain #isFull() full}, does
	 * nothing.
	 * 
	 * @param index the index to insert the tile at
	 * @param tile  the tile to try to add
	 * @return {@code true} iff this stack has changed
	 */
	default boolean offer(int index, T tile) {
		if (isFull())
			return false;
		add(index, tile);
		return true;
	}

	default boolean offerClosest(T tile) {
		return offer(0, tile);
	}

	default boolean offerFarthest(T tile) {
		return offer(size(), tile);
	}

	default T removeClosest() {
		return remove(0);
	}

	default T removeFarthest() {
		return remove(size() - 1);
	}

	default T poll(int index) {
		if (size() <= index)
			return null;
		return remove(index);
	}

	default T pollClosest() {
		return poll(0);
	}

	default T pollFarthest() {
		return poll(size() - 1);
	}

	@Override
	default boolean add(T tile) {
		addFarthest(tile);
		return true;
	}

}
