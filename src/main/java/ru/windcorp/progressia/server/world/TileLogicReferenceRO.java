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
package ru.windcorp.progressia.server.world;

import ru.windcorp.progressia.common.world.TileDataReference;
import ru.windcorp.progressia.common.world.TileDataReferenceRO;
import ru.windcorp.progressia.common.world.generic.TileGenericReferenceRO;
import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.server.world.block.BlockLogic;
import ru.windcorp.progressia.server.world.tile.TileLogic;

/**
 * A {@link TileGenericReferenceRO TileReference} for a {@link TileData} that
 * provides convenient access to the matching {@link TileLogic} instance.
 * <p>
 * For all methods other than {@link #get()}, {@link #getStack()} and
 * {@link #getDataReference()},
 * <tt>logicRef.<i>method</i>() == logicRef.getDataReference().<i>method</i>()</tt>.
 */
public interface TileLogicReferenceRO
	extends TileGenericReferenceRO<BlockLogic, TileLogic, TileLogicStackRO, TileLogicReferenceRO, ChunkLogicRO> {

	/**
	 * Returns a reference to the {@link TileData} that this object references.
	 * 
	 * @return a {@link TileDataReference} equivalent to this object
	 */
	TileDataReferenceRO getDataReference();

	/**
	 * Returns the {@link TileData} that is referenced by this object, or
	 * {@code null} if the tile does not exist.
	 * 
	 * @return the relevant {@link TileData}
	 */
	default TileData getData() {
		return getDataReference().get();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see #getData()
	 */
	@Override
	TileLogic get();

	/*
	 * Refer to #getDataReference() by default
	 */
	
	@Override
	default int getIndex() {
		return getDataReference().getIndex();
	}

	@Override
	default int getTag() {
		return getDataReference().getTag();
	}

	@Override
	default boolean isValid() {
		return getDataReference().isValid();
	}

}
