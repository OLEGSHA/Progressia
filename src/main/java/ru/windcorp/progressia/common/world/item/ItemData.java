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

import ru.windcorp.progressia.common.state.IntStateField;
import ru.windcorp.progressia.common.state.StatefulObject;
import ru.windcorp.progressia.common.world.generic.ItemGeneric;

/**
 * A collection of identical items identified by their ID, properties and
 * amount, able to reside in a slot. Also known as an item stack.
 * <p>
 * An empty stack does not have an {@code ItemData} representation; stack size
 * is at least 1.
 */
public abstract class ItemData extends StatefulObject implements ItemGeneric {

	private final IntStateField size = field("Core:Size").setShared().ofInt().build();

	public ItemData(String id) {
		super(ItemDataRegistry.getInstance(), id);
		size.setNow(this, 1);
	}

	/**
	 * Returns the amount of individual items represented by this item stack.
	 * 
	 * @return the size of this stack
	 */
	public final int getSize() {
		return size.get(this);
	}

	/**
	 * Sets the amount of items represented by this item stack.
	 * 
	 * @param size the new size of this stack, strictly positive
	 */
	public final void setSizeNow(int size) {
		if (size <= 0) {
			throw new IllegalArgumentException("size cannot be negative, given size " + size);
		}
		this.size.setNow(this, size);
	}

	/**
	 * Computes and returns the total mass of this item stack. It is defined as
	 * the item's unit mass (see {@link #getUnitMass()}) multiplied by the
	 * amount of items in the stack.
	 * 
	 * @return the mass of this stack
	 * @see #getUnitMass()
	 */
	public final float getMass() {
		return getUnitMass() * getSize();
	}

	/**
	 * Computes and returns the mass of a single unit (single item) of this item
	 * stack.
	 * 
	 * @return the mass of a single item in this stack
	 * @see #getMass()
	 */
	public abstract float getUnitMass();

	/**
	 * Computes and returns the total volume of this item stack. It is defined
	 * as the item's unit volume (see {@link #getUnitVolume()}) multiplied by
	 * the amount of items in the stack.
	 * 
	 * @return the mass of this stack
	 * @see #getUnitVolume()
	 */
	public final float getVolume() {
		return getUnitVolume() * getSize();
	}

	/**
	 * Computes and returns the volume of a single unit (single item) of this
	 * item stack.
	 * 
	 * @return the volume of a single item in this stack
	 * @see #getVolume()
	 */
	public abstract float getUnitVolume();

}
