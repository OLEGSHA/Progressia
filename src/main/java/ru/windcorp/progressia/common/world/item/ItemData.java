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
package ru.windcorp.progressia.common.world.item;

import ru.windcorp.progressia.common.state.StatefulObject;
import ru.windcorp.progressia.common.world.generic.ItemGeneric;

/**
 * An item identified by its ID and properties, able to reside in a slot.
 */
public abstract class ItemData extends StatefulObject implements ItemGeneric {

	public ItemData(String id) {
		super(ItemDataRegistry.getInstance(), id);
	}

	/**
	 * Computes and returns the mass of a single unit (single item) of this
	 * item.
	 * 
	 * @return the mass of this item
	 */
	public abstract float getMass();

	/**
	 * Computes and returns the volume of a single unit (single item) of this
	 * item stack.
	 * 
	 * @return the volume of this item
	 */
	public abstract float getVolume();

}
