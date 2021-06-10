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

package ru.windcorp.progressia.server.world.tile;

import ru.windcorp.progressia.common.world.tile.TileData;
import ru.windcorp.progressia.common.world.tile.TileDataStack;
import ru.windcorp.progressia.common.world.tile.TileReference;

public interface TileTickContext extends TSTickContext {

	/*
	 * Specifications
	 */

	/**
	 * Returns the current layer.
	 * 
	 * @return the layer that the tile being ticked occupies in the tile stack
	 */
	int getLayer();

	/*
	 * Getters
	 */

	default TileLogic getTile() {
		TileLogicStack stack = getTLSOrNull();
		if (stack == null)
			return null;
		return stack.get(getLayer());
	}

	default TileData getTileData() {
		TileDataStack stack = getTDSOrNull();
		if (stack == null)
			return null;
		return stack.get(getLayer());
	}

	default TileReference getReference() {
		return getTDS().getReference(getLayer());
	}

	default int getTag() {
		return getTDS().getTagByIndex(getLayer());
	}

	/*
	 * Contexts
	 */

	/*
	 * Convenience methods - changes
	 */

	default void removeThisTile() {
		getAccessor().removeTile(getBlockInWorld(), getFace(), getTag());
	}

}
