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
import java.util.Objects;
import java.util.RandomAccess;
import java.util.function.Consumer;

import glm.vec._3.i.Vec3i;
import ru.windcorp.progressia.common.world.Coordinates;
import ru.windcorp.progressia.common.world.rels.RelFace;

// @formatter:off
public interface TileGenericStackRO<
	B  extends BlockGeneric,
	T  extends TileGeneric,
	TS extends TileGenericStackRO     <B, T, TS, TR, C>,
	TR extends TileGenericReferenceRO <B, T, TS, TR, C>,
	C  extends ChunkGenericRO         <B, T, TS, TR, C>
> extends List<T>, RandomAccess {
// @formatter:on

	public static interface TSConsumer<T> {
		void accept(int layer, T tile);
	}

	public static final int TILES_PER_FACE = 8;

	Vec3i getBlockInChunk(Vec3i output);

	C getChunk();

	RelFace getFace();

	TR getReference(int index);

	int getIndexByTag(int tag);

	int getTagByIndex(int index);

	default Vec3i getBlockInWorld(Vec3i output) {
		// This is safe
		return Coordinates.getInWorld(getChunk().getPosition(), getBlockInChunk(output), output);
	}

	default boolean isFull() {
		return size() >= TILES_PER_FACE;
	}

	default T getClosest() {
		return get(0);
	}

	default T getFarthest() {
		return get(size() - 1);
	}

	default void forEach(TSConsumer<T> action) {
		Objects.requireNonNull(action, "action");
		for (int i = 0; i < size(); ++i) {
			action.accept(i, get(i));
		}
	}

	@Override
	default void forEach(Consumer<? super T> action) {
		Objects.requireNonNull(action, "action");
		for (int i = 0; i < size(); ++i) {
			action.accept(get(i));
		}
	}

	default T findClosest(String id) {
		Objects.requireNonNull(id, "id");

		for (int i = 0; i < size(); ++i) {
			T tile = get(i);
			if (tile.getId().equals(id)) {
				return tile;
			}
		}

		return null;
	}

	default T findFarthest(String id) {
		Objects.requireNonNull(id, "id");

		for (int i = 0; i < size(); ++i) {
			T tile = get(i);
			if (tile.getId().equals(id)) {
				return tile;
			}
		}

		return null;
	}

	default boolean contains(String id) {
		return findClosest(id) != null;
	}

	default B getHost() {
		return getChunk().getBlock(getBlockInChunk(null));
	}

}
