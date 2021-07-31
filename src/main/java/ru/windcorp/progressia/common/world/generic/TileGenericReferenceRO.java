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

/**
 * A reference to a single tile in a tile stack. A {@code TileReference} remains
 * valid until the tile is removed from its stack.
 * <p>
 * Tile reference objects may be reused for other tiles; {@link #isValid()} only
 * shows if there exists <em>some</em> tile that this object references; it may
 * or may not be the tile this reference was acquired for. It is the
 * responsibility of the programmer to discard references when the tile is
 * removed.
 */
// @formatter:off
public interface TileGenericReferenceRO<
	B  extends BlockGeneric,
	T  extends TileGeneric,
	TS extends TileGenericStackRO     <B, T, TS, TR, C>,
	TR extends TileGenericReferenceRO <B, T, TS, TR, C>,
	C  extends ChunkGenericRO         <B, T, TS, TR, C>
> {
// @formatter:on

	/**
	 * Gets the index that the referenced tile currently occupies. This value
	 * may change as tiles are added to or removed from the stack.
	 * 
	 * @return the index of the tile or {@code -1} if this reference is invalid
	 */
	int getIndex();

	/**
	 * Gets the tile stack that contains the referenced tile.
	 * 
	 * @return the tile stack of the relevant tile or {@code null} if this
	 *         reference is invalid.
	 */
	TS getStack();

	/**
	 * Gets the tile that this object references.
	 * 
	 * @return the relevant tile or {@code null} if this reference is invalid
	 */
	default T get() {
		return getStack().get(getIndex());
	}

	/**
	 * Checks whether this reference is valid. A reference is valid if it points
	 * to <em>some</em> tile; it may or may not be the tile that this reference
	 * was acquired for. (A tile reference can only change the referenced tile
	 * after the previous tile is removed from the stack.)
	 * 
	 * @return {@code true} iff there exists a tile that this reference points
	 *         to.
	 */
	default boolean isValid() {
		return get() != null;
	}

	/**
	 * Gets the tag of the referenced tile.
	 * 
	 * @return the tag or {@code -1} iff this reference is invalid.
	 */
	default int getTag() {
		TS tileStack = getStack();
		if (tileStack == null) {
			return -1;
		} else {
			return tileStack.getTagByIndex(getIndex());
		}
	}

}
